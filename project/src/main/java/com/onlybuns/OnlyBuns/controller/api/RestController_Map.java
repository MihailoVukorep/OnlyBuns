package com.onlybuns.OnlyBuns.controller.api;

import com.onlybuns.OnlyBuns.service.Service_Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RestController_Map {

    @Autowired
    private Service_Map service_map;

    @GetMapping("/api/map/locations")
    public ResponseEntity<List<String>> get_api_map_locations() {
        return service_map.get_api_map_locations();
    }
}
