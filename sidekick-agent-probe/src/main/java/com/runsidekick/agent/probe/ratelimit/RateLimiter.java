package com.runsidekick.agent.probe.ratelimit;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * @author serkan
 */
public class RateLimiter {

    private static final int MILLISECONDS_IN_MINUTE = 1000 * 60;
    private static final int RATE_LIMIT_WINDOW = 4;
    private static final int RATE_LIMIT_IDX_MASK = RATE_LIMIT_WINDOW - 1;
    private static final int LIMIT_IN_MINUTE = 1000;

    private final AtomicReferenceArray<RateLimitInfo> rateLimitInfos = new AtomicReferenceArray<>(RATE_LIMIT_WINDOW);

    public RateLimitResult checkRateLimit(long currentTime) {
        long currentMinute = currentTime / MILLISECONDS_IN_MINUTE;
        int rateLimitInfoIdx = (int) (currentMinute & RATE_LIMIT_IDX_MASK);
        RateLimitInfo rateLimitInfo = rateLimitInfos.get(rateLimitInfoIdx);
        if (rateLimitInfo == null) {
            rateLimitInfo = setRateLimitInfo(rateLimitInfoIdx, null, currentMinute);
        } else {
            if (rateLimitInfo.minute < currentMinute) {
                rateLimitInfo = setRateLimitInfo(rateLimitInfoIdx, rateLimitInfo, currentMinute);
            } else if (rateLimitInfo.minute > currentMinute) {
                // Normally this case should not happen, as there is enough window to prevent overlapping
                return RateLimitResult.OK;
            }
        }
        long count = rateLimitInfo.counter.incrementAndGet();
        if (count < LIMIT_IN_MINUTE) {
            return RateLimitResult.OK;
        } else if (count == LIMIT_IN_MINUTE) {
            return RateLimitResult.HIT;
        } else {
            return RateLimitResult.EXCEEDED;
        }
    }

    private RateLimitInfo setRateLimitInfo(int idx, RateLimitInfo existingRateLimitInfo, long currentMinute) {
        RateLimitInfo newRateLimitInfo = new RateLimitInfo(currentMinute);
        boolean set = rateLimitInfos.compareAndSet(idx, existingRateLimitInfo, newRateLimitInfo);
        if (set) {
            return newRateLimitInfo;
        } else {
            return rateLimitInfos.get(idx);
        }
    }

    private static class RateLimitInfo {

        private final long minute;
        private final AtomicLong counter = new AtomicLong();

        private RateLimitInfo(long minute) {
            this.minute = minute;
        }

    }

}
