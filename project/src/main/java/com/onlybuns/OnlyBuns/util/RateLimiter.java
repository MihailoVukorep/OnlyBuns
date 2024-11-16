package com.onlybuns.OnlyBuns.util;

import java.time.Instant;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimiter {

    private int maxAttempts = 5;
    private long timeWindow = 60 * 1000; // 1 minute in milliseconds

    // Rate limiter map: ip -> queue of attempt timestamps
    private Map<String, Queue<Long>> loginAttempts = new ConcurrentHashMap<>();

    public RateLimiter() {

    }

    public RateLimiter(int maxAttempts, long timeWindow) {
        this.maxAttempts = maxAttempts;
        this.timeWindow = timeWindow;
    }

    public boolean isRateLimited(String ip) {
        long currentTime = Instant.now().toEpochMilli();

        // Retrieve or initialize the login attempts queue for the user
        loginAttempts.putIfAbsent(ip, new LinkedList<>());
        Queue<Long> attempts = loginAttempts.get(ip);

        // Remove attempts that are outside the time window
        while (!attempts.isEmpty() && currentTime - attempts.peek() > timeWindow) {
            attempts.poll();
        }

        // Check if the user has reached the max attempts within the time window
        if (attempts.size() >= maxAttempts) {
            return true;
        }

        // Record the current attempt and proceed
        attempts.offer(currentTime);
        return false;
    }

}
