package com.runsidekick.agent.broker.client;

import com.runsidekick.agent.broker.BrokerCredentials;
import com.runsidekick.agent.broker.BrokerMessageCallback;
import com.runsidekick.agent.broker.client.impl.OkHttpWebSocketBrokerClient;

import java.util.concurrent.CompletableFuture;

/**
 * @author serkan
 */
public final class BrokerClientFactory {

    private BrokerClientFactory() {
    }

    public static BrokerClient createWebSocketClient(String host, int port,
                                                     BrokerCredentials brokerCredentials,
                                                     BrokerMessageCallback brokerMessageCallback,
                                                     CompletableFuture connectedFuture,
                                                     CompletableFuture closedFuture) throws Exception {
        return new OkHttpWebSocketBrokerClient(
                host, port, brokerCredentials,
                brokerMessageCallback, null, connectedFuture, closedFuture);
    }

}
