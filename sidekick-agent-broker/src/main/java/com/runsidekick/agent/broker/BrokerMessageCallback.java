package com.runsidekick.agent.broker;

import com.runsidekick.agent.broker.client.BrokerClient;

/**
 * @author serkan
 */
public interface BrokerMessageCallback {

    void onMessage(BrokerClient brokerClient, byte[] message);

    void onMessage(BrokerClient brokerClient, String message);

}
