package com.onlybuns.OnlyBuns.util;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class FollowRateLimiter {
    private static final int MAX_FOLLOWS = 50;
    private static final long TIME_WINDOW = 60; // seconds

    private final Map<Long, Queue<Long>> followAttempts = new ConcurrentHashMap<>();

    public synchronized boolean canFollow(Long userId) {
        long now = Instant.now().toEpochMilli();
        followAttempts.putIfAbsent(userId, new LinkedList<>());
        Queue<Long> attempts = followAttempts.get(userId);

        while (!attempts.isEmpty() && now - attempts.peek() > TIME_WINDOW) {
            attempts.poll();
        }

        if (attempts.size() >= MAX_FOLLOWS) {
            return false;
        }

        attempts.add(now);
        return true;
    }

    public void clear(Long userId) {
        followAttempts.remove(userId);
    }
}