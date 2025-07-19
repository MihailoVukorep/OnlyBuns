package com.onlybuns.OnlyBuns.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.onlybuns.OnlyBuns.model.Coordinate;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class Service_Location {

        private final RestTemplate restTemplate = new RestTemplate();

        @Cacheable(value="location", unless = "#result == null")
        public Coordinate resolveCoordinates(String locationName) {
            if (locationName.matches("^[-+]?\\d{1,2}\\.\\d+\\s*,\\s*[-+]?\\d{1,3}\\.\\d+$")) {
                try {
                    String[] parts = locationName.split("\\s*,\\s*");
                    double lat = Double.parseDouble(parts[0]);
                    double lon = Double.parseDouble(parts[1]);
                    return new Coordinate(lat, lon);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            try {
                String url = "https://nominatim.openstreetmap.org/search?q=" +
                        URLEncoder.encode(locationName, StandardCharsets.UTF_8) +
                        "&format=json&limit=1";

                HttpHeaders headers = new HttpHeaders();
                headers.set("User-Agent", "SpringApp/1.0 (email@example.com)");

                HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
                ResponseEntity<JsonNode> response = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        requestEntity,
                        JsonNode.class
                );

                JsonNode body = response.getBody();
                if (body != null && body.isArray() && body.size() > 0) {
                    JsonNode first = body.get(0);
                    double lat = first.get("lat").asDouble();
                    double lon = first.get("lon").asDouble();
                    return new Coordinate(lat, lon);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
}

