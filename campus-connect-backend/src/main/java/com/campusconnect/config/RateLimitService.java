package com.campusconnect.config;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class RateLimitService {

    private final RateLimitProperties properties;
    private final Map<String, LimitState> states = new ConcurrentHashMap<>();

    public RateLimitService(RateLimitProperties properties) {
        this.properties = properties;
    }

    public Decision tryConsume(
            String key,
            int capacity,
            long windowSeconds,
            boolean exponentialBackoff) {

        if (capacity <= 0 || windowSeconds <= 0) {
            return Decision.allowed();
        }

        long now = System.currentTimeMillis();
        LimitState state = states.computeIfAbsent(key, ignored -> new LimitState(now));

        synchronized (state) {
            long windowMillis = Duration.ofSeconds(windowSeconds).toMillis();

            if (now - state.windowStartedAt >= windowMillis) {
                state.windowStartedAt = now;
                state.requestCount = 0;
            }

            if (state.blockedUntil > now) {
                return Decision.denied(remainingSeconds(state.blockedUntil, now));
            }

            if (state.requestCount < capacity) {
                state.requestCount++;
                return Decision.allowed();
            }

            if (!exponentialBackoff) {
                return Decision.denied(remainingSeconds(state.windowStartedAt + windowMillis, now));
            }

            state.violationCount++;
            long backoffSeconds = calculateBackoffSeconds(state.violationCount);
            state.blockedUntil = now + Duration.ofSeconds(backoffSeconds).toMillis();
            return Decision.denied(backoffSeconds);
        }
    }

    public void removeExpiredStates() {
        long now = System.currentTimeMillis();
        states.entrySet().removeIf(entry -> {
            LimitState state = entry.getValue();
            synchronized (state) {
                return state.blockedUntil <= now
                        && now - state.windowStartedAt > Duration.ofHours(1).toMillis();
            }
        });
    }

    public int getStateCount() {
        return states.size();
    }

    public int getMaxTrackedKeys() {
        return properties.getMaxTrackedKeys();
    }

    private long calculateBackoffSeconds(long violationCount) {
        long base = Math.max(1, properties.getBackoffBaseSeconds());
        long maximum = Math.max(base, properties.getBackoffMaxSeconds());
        long multiplier = violationCount >= 62 ? Long.MAX_VALUE : 1L << Math.max(0, violationCount - 1);

        if (multiplier > maximum / base) {
            return maximum;
        }

        return Math.min(maximum, base * multiplier);
    }

    private long remainingSeconds(long endTime, long now) {
        long remainingMillis = Math.max(1, endTime - now);
        return Math.max(1, (remainingMillis + 999) / 1000);
    }

    public static final class Decision {
        private final boolean allowed;
        private final long retryAfterSeconds;

        private Decision(boolean allowed, long retryAfterSeconds) {
            this.allowed = allowed;
            this.retryAfterSeconds = retryAfterSeconds;
        }

        public static Decision allowed() {
            return new Decision(true, 0);
        }

        public static Decision denied(long retryAfterSeconds) {
            return new Decision(false, retryAfterSeconds);
        }

        public boolean isAllowed() {
            return allowed;
        }

        public long getRetryAfterSeconds() {
            return retryAfterSeconds;
        }
    }

    private static final class LimitState {
        private long windowStartedAt;
        private int requestCount;
        private long blockedUntil;
        private long violationCount;

        private LimitState(long windowStartedAt) {
            this.windowStartedAt = windowStartedAt;
        }
    }
}
