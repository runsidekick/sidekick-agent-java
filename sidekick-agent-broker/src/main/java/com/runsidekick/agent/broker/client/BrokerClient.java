package com.runsidekick.agent.broker.client;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author serkan
 */
public interface BrokerClient {

    void send(String msg) throws IOException;
    void send(byte[] msg) throws IOException;
    void send(byte[] msg, int off, int len) throws IOException;

    void sendCloseMessage(int code, String reason) throws IOException;

    void close();

    boolean isClosed();

    boolean waitUntilClosed();
    boolean waitUntilClosed(long timeout, TimeUnit unit);

    void destroy();

}
