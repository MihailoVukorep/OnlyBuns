package com.onlybuns.OnlyBuns.util;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import java.time.format.DateTimeFormatter;

@Component
public class ActiveUserMetrics {

    private final Map<String, Double> hourlyUserCount = new ConcurrentHashMap<>();
    private static final DateTimeFormatter HOUR_FORMATTER = DateTimeFormatter.ofPattern("HH:00");

    @Autowired
    public ActiveUserMetrics(MeterRegistry meterRegistry) {
        for (int i = 0; i <= 24; i++) {
            String hour = LocalDateTime.now().minusHours(24 - i)
                    .truncatedTo(ChronoUnit.HOURS)
                    .format(HOUR_FORMATTER);

            hourlyUserCount.put(hour, 0.0);

            meterRegistry.gauge("active_users_per_hour",
                    Tags.of("hour", hour),
                    hourlyUserCount,
                    map -> map.getOrDefault(hour, 0.0));
        }
    }

    public void updateHourlyCounts(Map<String, Long> counts) {
        hourlyUserCount.clear();
        for (Map.Entry<String, Long> entry : counts.entrySet()) {
            hourlyUserCount.put(entry.getKey(), entry.getValue().doubleValue());
        }
    }
}


