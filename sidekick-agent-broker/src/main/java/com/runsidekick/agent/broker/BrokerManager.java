package com.runsidekick.agent.broker;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import com.runsidekick.agent.broker.application.ApplicationStatusProvider;
import com.runsidekick.agent.broker.domain.ApplicationStatus;
import com.runsidekick.agent.broker.handler.response.ResponseHandler;
import com.runsidekick.agent.broker.request.impl.FilterLogPointsRequest;
import com.runsidekick.agent.broker.request.impl.FilterTracePointsRequest;
import com.runsidekick.agent.broker.request.impl.GetConfigRequest;
import com.runsidekick.agent.broker.response.MutableResponse;
import com.runsidekick.agent.broker.response.Response;
import com.runsidekick.agent.broker.response.impl.ErrorResponse;
import com.runsidekick.agent.broker.client.BrokerClientFactory;
import com.runsidekick.agent.broker.event.Event;
import com.runsidekick.agent.broker.event.MutableEvent;
import com.runsidekick.agent.broker.event.impl.ApplicationStatusEvent;
import com.runsidekick.agent.broker.client.BrokerClient;
import com.runsidekick.agent.broker.client.BrokerClientHandshakeException;
import com.runsidekick.agent.broker.handler.request.RequestHandler;
import com.runsidekick.agent.api.broker.publisher.EventPublisher;
import com.runsidekick.agent.api.broker.publisher.RequestPublisher;
import com.runsidekick.agent.broker.request.Request;
import com.runsidekick.agent.broker.support.BaseProbeSupport;
import com.runsidekick.agent.core.app.Application;
import com.runsidekick.agent.core.app.ApplicationInfo;
import com.runsidekick.agent.core.instance.InstanceDiscovery;
import com.runsidekick.agent.core.logger.LoggerFactory;
import com.runsidekick.agent.core.terminate.EnvironmentTerminator;
import com.runsidekick.agent.core.terminate.EnvironmentTerminatorManager;
import com.runsidekick.agent.core.util.ExecutorUtils;
import com.runsidekick.agent.core.util.PropertyUtils;
import com.runsidekick.agent.core.util.StringUtils;
import com.runsidekick.agent.core.util.ThreadUtils;
import org.json.JSONObject;
import org.slf4j.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author serkan
 */
public class BrokerManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrokerManager.class);

    public static final boolean BROKER_ENABLE =
            PropertyUtils.getBooleanProperty("sidekick.agent.broker.enable", true);
    public static final int BROKER_PORT =
            PropertyUtils.getIntegerProperty("sidekick.agent.broker.port", 443);
    public static final String BROKER_HOST =
            PropertyUtils.getStringProperty("sidekick.agent.broker.host", "wss://broker.service.runsidekick.com");
    public static final String BROKER_CLIENT =
            PropertyUtils.getStringProperty("sidekick.agent.broker.client");
    public static final String DEFAULT_CLIENT = "default";
    public static final String API_KEY =
            PropertyUtils.getApiKey();
    private static final String HOST_NAME = getHostName();
    private static final int MAX_IN_FLIGHT_EVENT_COUNT = 10000;
    private static final int APPLICATION_STATUS_PUBLISH_PERIOD_IN_SECS = 60;
    private static final String MESSAGE_REQUEST_TYPE = "Request";
    private static final String MESSAGE_RESPONSE_TYPE = "Response";

    private static boolean initialized = false;
    private static final AtomicReference<BrokerClient> brokerClientRef = new AtomicReference<>();
    private static final AtomicBoolean brokerClientClosed = new AtomicBoolean(false);
    private static final SystemBrokerMessageCallback systemBrokerMessageCallback = new SystemBrokerMessageCallback();
    private static final Map<String, RequestHandler> requestHandlerMap =
            InstanceDiscovery.instancesOf(RequestHandler.class).
                    stream().
                    collect(Collectors.toMap(RequestHandler::getRequestName, rh -> rh));
    private static final Map<String, ResponseHandler> responseHandlerMap =
            InstanceDiscovery.instancesOf(ResponseHandler.class).
                    stream().
                    collect(Collectors.toMap(ResponseHandler::getResponseName, rh -> rh));
    private static final List<ApplicationStatusProvider> applicationStatusProviderList =
            InstanceDiscovery.instancesOf(ApplicationStatusProvider.class);
    private static final List<BaseProbeSupport> probeSupportList =
            InstanceDiscovery.instancesOf(BaseProbeSupport.class);
    private static final ThreadLocal<ObjectMapper> threadLocalObjectMapper =
            ThreadLocal.withInitial(() -> createObjectMapper());

    private static Thread brokerConnectorThread;
    private static ExecutorService eventPublisherExecutorService;
    private static ScheduledExecutorService applicationStatusPublisherExecutorService;
    private static volatile EventPublisher eventPublisher = new BrokerClientEventPublisher();
    private static volatile RequestPublisher requestPublisher = new BrokerClientRequestPublisher();

    private static volatile boolean attached = true;

    private BrokerManager() {
    }

    public static synchronized void ensureInitialized() {
        if (!initialized) {
            doInit();
            initialized = true;
        }
    }

    private static ObjectMapper createObjectMapper() {
        return new ObjectMapper().
                setSerializationInclusion(JsonInclude.Include.NON_NULL).
                configure(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED, true).
                configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false).
                configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).
                registerModule(new AfterburnerModule());
    }

    private static void doInit() {
        if (StringUtils.hasValue(API_KEY)) {
            if (!BROKER_ENABLE) {
                LOGGER.debug("Skipping connecting to broker as it is disable");
                return;
            }
            eventPublisherExecutorService =
                    ExecutorUtils.newMaxSizedExecutorService(
                            MAX_IN_FLIGHT_EVENT_COUNT, "broker-event-publisher");
            applicationStatusPublisherExecutorService =
                    ExecutorUtils.newScheduledExecutorService("broker-app-status-publisher");
            applicationStatusPublisherExecutorService.scheduleAtFixedRate(
                    new ApplicationStatusPublisher(),
                    APPLICATION_STATUS_PUBLISH_PERIOD_IN_SECS,
                    APPLICATION_STATUS_PUBLISH_PERIOD_IN_SECS,
                    TimeUnit.SECONDS);
            brokerConnectorThread = connectToBroker(systemBrokerMessageCallback);
            EnvironmentTerminatorManager.registerEnvironmentTerminator(new EnvironmentTerminator() {
                @Override
                public int order() {
                    return NORMAL;
                }

                @Override
                public void terminate() {
                    LOGGER.debug("Closing broker connection on shutdown ...");
                    brokerClientClosed.set(true);
                    if (brokerConnectorThread != null) {
                        brokerConnectorThread.interrupt();
                        try {
                            brokerConnectorThread.join(TimeUnit.SECONDS.toMillis(10));
                        } catch (InterruptedException e) {
                        }
                    }
                }
            });
        }
    }

    private static class SystemBrokerMessageCallback implements BrokerMessageCallback {

        @Override
        public void onMessage(BrokerClient brokerClient, byte[] message) {
            LOGGER.warn("Binary message is not expected to handle");
        }

        @Override
        public void onMessage(BrokerClient brokerClient, String message) {
            handleMessage(brokerClient, message);
        }

        private void handleMessage(BrokerClient brokerClient, String message) {
            try {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Handling message: {}", message);
                }

                ObjectMapper objectMapper = threadLocalObjectMapper.get();
                JSONObject messageObj = new JSONObject(message);
                String name = null;
                if (messageObj.has("name")) {
                    name = messageObj.getString("name");
                }
                if (name == null) {
                    LOGGER.error("No message name could be found in message: {}", message);
                    return;
                }

                if ((attached && name.equals("AttachRequest"))
                    || (!attached && !name.equals("AttachRequest"))) {
                    return;
                }

                String type = null;
                if (messageObj.has("type") && !messageObj.isNull("type")) {
                    type = messageObj.getString("type");
                }
                if (name == null) {
                    LOGGER.error("No message type could be found in message: {}", message);
                    return;
                }

                if (MESSAGE_REQUEST_TYPE.equals(type)) {
                    RequestHandler requestHandler = requestHandlerMap.get(name);
                    if (requestHandler != null) {
                        Request request = (Request) objectMapper.readValue(message, requestHandler.getRequestClass());
                        Response response;
                        try {
                            response = requestHandler.handleRequest(request);
                        } catch (Throwable t) {
                            LOGGER.error("Error occurred while handling request for message with name {}: {}", name, message);
                            response = new ErrorResponse(request.getId(), request.getClient(), t);
                        }
                        if (response instanceof MutableResponse) {
                            MutableResponse mutableResponse = (MutableResponse) response;
                            if (mutableResponse.getRequestId() == null) {
                                mutableResponse.setRequestId(request.getId());
                            }
                            if (mutableResponse.getClient() == null) {
                                mutableResponse.setClient(request.getClient());
                            }
                            ApplicationInfo applicationInfo = Application.getApplicationInfo();
                            if (applicationInfo != null) {
                                if (mutableResponse.getApplicationName() == null) {
                                    mutableResponse.setApplicationName(applicationInfo.getApplicationName());
                                }
                                if (mutableResponse.getApplicationInstanceId() == null) {
                                    mutableResponse.setApplicationInstanceId(applicationInfo.getApplicationInstanceId());
                                }
                            }
                        }
                        String responseRaw = objectMapper.writeValueAsString(response);
                        brokerClient.send(responseRaw);
                    } else {
                        LOGGER.warn("No request handler could be found for message with name {}: {}", name, message);
                    }
                } else if (MESSAGE_RESPONSE_TYPE.equals(type)) {
                    ResponseHandler responseHandler = responseHandlerMap.get(name);
                    if (responseHandler != null) {
                        Response response = (Response) objectMapper.readValue(message, responseHandler.getResponseClass());
                        try {
                            responseHandler.handleResponse(response);
                        } catch (Throwable t) {
                            LOGGER.error("Error occurred while handling response for message with name {}: {}", name, message);
                        }
                    } else {
                        LOGGER.warn("No response handler could be found for message with name {}: {}", name, message);
                    }

                }
            } catch (Throwable error) {
                LOGGER.error("Error occurred while handling message: " + message, error);
            }
        }

    }

    private static class ApplicationStatusPublisher implements Runnable {

        @Override
        public void run() {
            try {
                publishApplicationStatus();
            } catch (Throwable t) {
                LOGGER.error("Unable to publish application status", t);
            }
        }

    }

    private static class BrokerClientEventPublisher implements EventPublisher {

        @Override
        public void publishEvent(String eventJson) {
            BrokerClient brokerClient = brokerClientRef.get();
            if (brokerClient != null) {
                try {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Sending event: {}", eventJson);
                    }
                    brokerClient.send(eventJson);
                } catch (Throwable error) {
                    LOGGER.error("Error occurred while publishing event: " + eventJson, error);
                }
            }
        }

    }

    private static class BrokerClientRequestPublisher implements RequestPublisher {

        @Override
        public void publishRequest(String requestJson) {
            BrokerClient brokerClient = brokerClientRef.get();
            if (brokerClient != null) {
                try {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Sending request: {}", requestJson);
                    }
                    brokerClient.send(requestJson);
                } catch (Throwable error) {
                    LOGGER.error("Error occurred while publishing request: " + requestJson, error);
                }
            }
        }

    }

    private static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return null;
        }
    }

    private static BrokerCredentials buildBrokerCredentials() {
        BrokerCredentials brokerCredentials = new BrokerCredentials();
        brokerCredentials.setApiKey(API_KEY);
        brokerCredentials.setHostName(HOST_NAME);
        ApplicationInfo applicationInfo = Application.getApplicationInfo();
        if (applicationInfo != null) {
            brokerCredentials.setAppName(applicationInfo.getApplicationName());
            brokerCredentials.setAppInstanceId(applicationInfo.getApplicationInstanceId());
            brokerCredentials.setAppVersion(applicationInfo.getApplicationVersion());
            brokerCredentials.setAppStage(applicationInfo.getApplicationStage());
            brokerCredentials.setRuntime(applicationInfo.getApplicationRuntime());
            Map<String, Object> appTags = applicationInfo.getApplicationTags();
            if (appTags != null) {
                for (Map.Entry<String, Object> e : appTags.entrySet()) {
                    String appTagName = e.getKey();
                    Object appTagValue = e.getValue();
                    brokerCredentials.addAppTag(appTagName, appTagValue.toString());
                }
            }
        }
        return brokerCredentials;
    }

    private static Thread connectToBroker(BrokerMessageCallback brokerMessageCallback) {
        Thread brokerConnectorThread = ThreadUtils.newDaemonThread(() -> {
            while (!brokerClientClosed.get()) {
                BrokerClient wsc = null;
                CompletableFuture connectedFuture = new CompletableFuture();
                CompletableFuture closedFuture = new CompletableFuture();

                LOGGER.info("Connecting to broker ...");

                try {
                    BrokerCredentials brokerCredentials = buildBrokerCredentials();

                    wsc = BrokerClientFactory.createWebSocketClient(
                            BROKER_HOST, BROKER_PORT,
                            brokerCredentials, brokerMessageCallback,
                            connectedFuture, closedFuture);
                    brokerClientRef.set(wsc);

                    connectedFuture.whenComplete((val, error) -> {
                        if (error != null) {
                            LOGGER.error("Connection to broker has failed: {}", ((Throwable) error).getMessage());
                        } else {
                            onBrokerConnected();
                        }
                    });

                    closedFuture.get();
                } catch (Throwable t) {
                    if (t instanceof ExecutionException) {
                        t = t.getCause();
                    }
                    if (t instanceof BrokerClientHandshakeException) {
                        LOGGER.error(t.getMessage());
                    } else {
                        if (!(t instanceof InterruptedException)) {
                            LOGGER.error("Error occurred while connecting to broker", t);
                        }
                    }
                } finally {
                    brokerClientRef.set(null);
                    if (wsc != null) {
                        if (!wsc.isClosed()) {
                            wsc.close();
                            wsc.waitUntilClosed(5, TimeUnit.SECONDS);
                        }
                        wsc.destroy();
                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            }
        }, "broker-connector");
        brokerConnectorThread.start();
        return brokerConnectorThread;
    }

    static void onBrokerConnected() {
        LOGGER.info("Connected to broker");
        getConfig();
        getExistingTracePoints();
        getExistingLogPoints();
    }

    private static void getConfig() {
        GetConfigRequest request = new GetConfigRequest();
        request.setId(UUID.randomUUID().toString());

        doPublishRequest(request);
    }

    private static void getExistingTracePoints() {
        ApplicationInfo applicationInfo = Application.getApplicationInfo();
        FilterTracePointsRequest request = new FilterTracePointsRequest(applicationInfo.getApplicationName(),
                applicationInfo.getApplicationVersion(), applicationInfo.getApplicationStage(),
                applicationInfo.getApplicationTags());
        request.setId(UUID.randomUUID().toString());

        doPublishRequest(request);
    }

    private static void getExistingLogPoints() {
        ApplicationInfo applicationInfo = Application.getApplicationInfo();
        FilterLogPointsRequest request = new FilterLogPointsRequest(applicationInfo.getApplicationName(),
                applicationInfo.getApplicationVersion(), applicationInfo.getApplicationStage(),
                applicationInfo.getApplicationTags());
        request.setId(UUID.randomUUID().toString());

        doPublishRequest(request);
    }

    private static void prepareEvent(Event event) {
        if (event instanceof MutableEvent) {
            MutableEvent mutableEvent = (MutableEvent) event;
            if (mutableEvent.getId() == null) {
                mutableEvent.setId(UUID.randomUUID().toString());
            }
            if (mutableEvent.getTime() == 0L) {
                mutableEvent.setTime(System.currentTimeMillis());
            }
            if (mutableEvent.getHostName() == null) {
                mutableEvent.setHostName(HOST_NAME);
            }
            ApplicationInfo applicationInfo = Application.getApplicationInfo();
            if (applicationInfo != null) {
                if (mutableEvent.getApplicationName() == null) {
                    mutableEvent.setApplicationName(applicationInfo.getApplicationName());
                }
                if (mutableEvent.getApplicationInstanceId() == null) {
                    mutableEvent.setApplicationInstanceId(applicationInfo.getApplicationInstanceId());
                }
            }
        }
    }

    private static void doPublishEvent(Event event) {
        try {
            prepareEvent(event);
            String eventJson = threadLocalObjectMapper.get().writeValueAsString(event);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Sending event: {}", eventJson);
            }
            eventPublisher.publishEvent(eventJson);
        } catch (Throwable error) {
            LOGGER.error("Error occurred while publishing event: " + event, error);
        }
    }

    private static void doPublishRequest(Request request) {
        try {
            String requestJson = threadLocalObjectMapper.get().writeValueAsString(request);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Sending request: {}", requestJson);
            }
            requestPublisher.publishRequest(requestJson);
        } catch (Throwable error) {
            LOGGER.error("Error occurred while publishing request: " + request, error);
        }
    }

    private static void doPublishSerializedEvent(String eventJson) {
        if (attached) {
            eventPublisher.publishEvent(eventJson);
        }
    }

    public static void publishEvent(Event event) {
        if (attached && eventPublisherExecutorService != null) {
            eventPublisherExecutorService.submit(() -> {
                doPublishEvent(event);
            });
        }
    }

    public static void publishEvent(Supplier<Event> eventSupplier) {
        if (attached && eventPublisherExecutorService != null) {
            eventPublisherExecutorService.submit(() -> {
                Event event = eventSupplier.get();
                doPublishEvent(event);
            });
        }
    }

    public static void serializeAndPublishEvent(Event event) {
        try {
            prepareEvent(event);
            String eventJson = threadLocalObjectMapper.get().writeValueAsString(event);
            if (eventPublisherExecutorService != null) {
                eventPublisherExecutorService.submit(() -> {
                    doPublishSerializedEvent(eventJson);
                });
            }
        } catch (Throwable error) {
            LOGGER.error("Error occurred while serializing event: " + event, error);
        }
    }

    public static void serializeAndPublishEvent(Supplier<Event> eventSupplier) {
        if (eventPublisherExecutorService != null) {
            eventPublisherExecutorService.submit(() -> {
                Event event = eventSupplier.get();
                try {
                    prepareEvent(event);
                    String eventJson = threadLocalObjectMapper.get().writeValueAsString(event);
                    doPublishSerializedEvent(eventJson);
                } catch (Throwable error) {
                    LOGGER.error("Error occurred while serializing event: " + event, error);
                }
            });
        }
    }

    public static boolean publishApplicationStatus() {
        return doPublishApplicationStatus(null);
    }

    public static boolean publishApplicationStatus(String client) {
        return doPublishApplicationStatus(client);
    }

    private static boolean doPublishApplicationStatus(String client) {
        ApplicationInfo applicationInfo = Application.getApplicationInfo();
        if (applicationInfo != null) {
            ApplicationStatus applicationStatus = new ApplicationStatus();
            applicationStatus.setName(applicationInfo.getApplicationName());
            applicationStatus.setInstanceId(applicationInfo.getApplicationInstanceId());
            applicationStatus.setVersion(applicationInfo.getApplicationVersion());
            applicationStatus.setStage(applicationInfo.getApplicationStage());
            applicationStatus.setRuntime(applicationInfo.getApplicationRuntime());
            applicationStatus.setHostName(HOST_NAME);
            Map<String, Object> tags = applicationInfo.getApplicationTags();
            if (tags != null) {
                for (Map.Entry<String, Object> e : tags.entrySet()) {
                    String tagName = e.getKey();
                    Object tagValue = e.getValue();
                    applicationStatus.addTag(tagName, tagValue.toString());
                }
            }
            for (ApplicationStatusProvider applicationStatusProvider : applicationStatusProviderList) {
                applicationStatusProvider.provide(applicationStatus, client);
            }
            publishEvent(new ApplicationStatusEvent(applicationStatus, client));
            return true;
        }
        return false;
    }

    public static EventPublisher getEventPublisher() {
        return eventPublisher;
    }

    public static void setEventPublisher(EventPublisher eventPublisher) {
        BrokerManager.eventPublisher = eventPublisher;
    }

    public static void attach() {
        attached = true;
        onBrokerConnected();
    }

    public static void detach() {
        attached = false;
        probeSupportList.forEach(probeSupport -> probeSupport.detach());
    }

}
