package com.runsidekick.agent.core.entity;

/**
 * Interface to specify order.
 *
 * @author serkan
 */
public interface Ordered {

    /**
     * Represents <b>lowest</b> order.
     */
    int LOWEST = Integer.MIN_VALUE;

    /**
     * Represents <b>low</b> order.
     */
    int LOW = Integer.MIN_VALUE / 2;

    /**
     * Represents <b>normal</b> order.
     */
    int NORMAL = 0;

    /**
     * Represents <b>high</b> order.
     */
    int HIGH = Integer.MAX_VALUE / 2;

    /**
     * Represents <b>highest</b> order.
     */
    int HIGHEST = Integer.MAX_VALUE;

    /**
     * Returns the order.
     *
     * @return the order
     */
    default int order() {
        return NORMAL;
    }

}
