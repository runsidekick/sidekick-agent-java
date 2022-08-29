package com.runsidekick.agent.broker.client.impl;

import com.runsidekick.agent.broker.BrokerCredentials;
import com.runsidekick.agent.broker.BrokerMessageCallback;
import com.runsidekick.agent.broker.client.BrokerClient;
import com.runsidekick.agent.core.logger.LoggerFactory;
import com.runsidekick.agent.core.util.ExecutorUtils;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author serkan
 */
public final class OkHttpWebSocketBrokerClient
        extends WebSocketListener
        implements BrokerClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(OkHttpWebSocketBrokerClient.class);

    private static final String API_KEY_HEADER_NAME = "x-sidekick-api-key";
    private static final String APP_INSTANCE_ID_HEADER_NAME = "x-sidekick-app-instance-id";
    private static final String APP_NAME_HEADER_NAME = "x-sidekick-app-name";
    private static final String APP_STAGE_HEADER_NAME = "x-sidekick-app-stage";
    private static final String APP_VERSION_HEADER_NAME = "x-sidekick-app-version";
    private static final String APP_HOSTNAME_HEADER_NAME = "x-sidekick-app-hostname";
    private static final String APP_RUNTIME_HEADER_NAME = "x-sidekick-app-runtime";
    private static final String APP_TAG_HEADER_NAME_PREFIX = "x-sidekick-app-tag-";

    private static final OkHttpClient baseClient =
            new OkHttpClient.Builder().
                    dispatcher(new Dispatcher(
                            ExecutorUtils.newCachedExecutorService("okhttp-dispatcher"))).
                    readTimeout(3,  TimeUnit.SECONDS).
                    pingInterval(30, TimeUnit.SECONDS).
                    build();

    private final OkHttpClient client;
    private final WebSocket webSocket;
    private final BrokerMessageCallback messageCallback;
    private final CompletableFuture<Boolean> connectedFuture;
    private final CompletableFuture<Boolean> closedFuture;

    public OkHttpWebSocketBrokerClient(String host, int port,
                                       BrokerCredentials brokerCredentials,
                                       BrokerMessageCallback messageCallback) {
        this(host, port, brokerCredentials, messageCallback, null, null, null);
    }

    public OkHttpWebSocketBrokerClient(String host, int port,
                                       BrokerCredentials brokerCredentials,
                                       BrokerMessageCallback messageCallback,
                                       Map<String, String> headers,
                                       CompletableFuture connectedFuture,
                                       CompletableFuture closedFuture) {
        this.messageCallback = messageCallback;
        this.connectedFuture =
                connectedFuture == null
                        ? new CompletableFuture()
                        : connectedFuture;
        this.closedFuture =
                closedFuture == null
                        ? new CompletableFuture()
                        : closedFuture;
        Request request = buildRequest(host, port, brokerCredentials, headers);
        this.client = baseClient.newBuilder().build();
        this.webSocket = client.newWebSocket(request, this);
    }

    private static Request buildRequest(String host, int port,
                                        BrokerCredentials brokerCredentials, Map<String, String> headers) {
        Request.Builder builder = new Request.Builder();
        String url = generateBrokerUrl(host, port);
        builder.url(url + "/app");
        if (brokerCredentials.getApiKey() != null) {
            builder.header(API_KEY_HEADER_NAME, brokerCredentials.getApiKey());
        }
        if (brokerCredentials.getAppInstanceId() != null) {
            builder.header(APP_INSTANCE_ID_HEADER_NAME, brokerCredentials.getAppInstanceId());
        }
        if (brokerCredentials.getAppName() != null) {
            builder.header(APP_NAME_HEADER_NAME, brokerCredentials.getAppName());
        }
        if (brokerCredentials.getAppStage() != null) {
            builder.header(APP_STAGE_HEADER_NAME, brokerCredentials.getAppStage());
        }
        if (brokerCredentials.getAppVersion() != null) {
            builder.header(APP_VERSION_HEADER_NAME, brokerCredentials.getAppVersion());
        }
        if (brokerCredentials.getHostName() != null) {
            builder.header(APP_HOSTNAME_HEADER_NAME, brokerCredentials.getHostName());
        }
        if (brokerCredentials.getRuntime() != null) {
            builder.header(APP_RUNTIME_HEADER_NAME, brokerCredentials.getRuntime());
        }
        Map<String, String> appTags = brokerCredentials.getAppTags();
        if (appTags != null) {
            for (Map.Entry<String, String> e : appTags.entrySet()) {
                String appTagName = e.getKey();
                String appTagValue = e.getValue();
                builder.header(APP_TAG_HEADER_NAME_PREFIX + appTagName, appTagValue);
            }
        }
        if (headers != null) {
            for (Map.Entry<String, String> e : headers.entrySet()) {
                String headerName = e.getKey();
                String headerValue = e.getValue();
                builder.header(headerName, headerValue);
            }
        }
        return builder.build();
    }

    private static String generateBrokerUrl(String host, int port) {
        if (host.startsWith("ws://") || host.startsWith("wss://")) {
            return host + ":" + port;
        } else {
            return "wss://" + host + ":" + port;
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean isConnected() {
        return connectedFuture.isDone() && !connectedFuture.isCompletedExceptionally();
    }

    public boolean waitUntilConnected() {
        try {
            return connectedFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            return false;
        }
    }

    public boolean waitUntilConnected(long timeout, TimeUnit unit) {
        try {
            return connectedFuture.get(timeout, unit);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            return false;
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void send(String message) throws IOException {
        if (!webSocket.send(message)) {
            throw new IOException("Unable to send message");
        }
    }

    @Override
    public void send(byte[] message) throws IOException {
        if (!webSocket.send(ByteString.of(message))) {
            throw new IOException("Unable to send message");
        }
    }

    @Override
    public void send(byte[] message, int off, int len) throws IOException {
        if (!webSocket.send(ByteString.of(message, off, len))) {
            throw new IOException("Unable to send message");
        }
    }

    @Override
    public void sendCloseMessage(int code, String reason) throws IOException {
        if (!webSocket.close(code, reason)) {
            throw new IOException("Unable to send message");
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void close() {
        try {
            webSocket.close(1000, null);
        } catch (Exception e) {
        }
    }

    @Override
    public void destroy() {
        try {
            webSocket.cancel();
        } catch (Exception e) {
        }
    }

    @Override
    public boolean isClosed() {
        return closedFuture.isDone() && !closedFuture.isCompletedExceptionally();
    }

    @Override
    public boolean waitUntilClosed() {
        try {
            return closedFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            return false;
        }
    }

    @Override
    public boolean waitUntilClosed(long timeout, TimeUnit unit) {
        try {
            return closedFuture.get(timeout, unit);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            return false;
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        LOGGER.debug("OPEN: " + response.message());
        connectedFuture.complete(true);
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("MESSAGE: " + text);
        }
        if (messageCallback != null) {
            messageCallback.onMessage(this, text);
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("MESSAGE: " + bytes.hex());
        }
        if (messageCallback != null) {
            messageCallback.onMessage(this, bytes.toByteArray());
        }
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        LOGGER.debug("CLOSING: " + code + " " + reason);
        webSocket.close(1000, null);
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        LOGGER.debug("CLOSED: " + code + " " + reason);
        closedFuture.complete(true);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        LOGGER.error("FAILED: " + t.getMessage());
        try {
            LOGGER.error(response.body().string());
        } catch (IOException e) { }
        if (!isConnected()) {
            connectedFuture.completeExceptionally(t);
        }
        closedFuture.completeExceptionally(t);
    }

}
