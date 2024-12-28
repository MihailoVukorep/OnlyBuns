package com.onlybuns.OnlyBuns.controller.api;

import com.onlybuns.OnlyBuns.dto.DTO_Get_Locations;
import com.onlybuns.OnlyBuns.service.Service_Map;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestController_Map {

    @Autowired
    private Service_Map service_map;

    @GetMapping("/api/map/locations")
    public ResponseEntity<DTO_Get_Locations> get_api_map_locations(HttpSession session) {
        return service_map.get_api_map_locations(session);
    }
}
