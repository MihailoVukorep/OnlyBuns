package com.onlybuns.OnlyBuns.logger;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
public class CacheChecker {
    @Autowired
    private CacheManager cacheManager;

    @PostConstruct
    public void checkCaches() {
        System.out.println(">>> CacheManager type: " + cacheManager.getClass().getName());
        System.out.println("Caches configured: " + cacheManager.getCacheNames());
    }
}

