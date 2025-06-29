package com.onlybuns.OnlyBuns.controller.api;

import com.onlybuns.OnlyBuns.dto.DTO_Get_Location;
import com.onlybuns.OnlyBuns.dto.DTO_Get_Locations;
import com.onlybuns.OnlyBuns.service.Service_Map;
import com.onlybuns.OnlyBuns.service.Service_RabbitCareMessageQueue;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestController_Map {

    @Autowired
    private Service_Map service_map;


    @Operation(summary = "all map locations (posts, vets, shelters)")
    @GetMapping("/api/map/locations")
    public ResponseEntity<DTO_Get_Locations> get_api_map_locations(HttpSession session) {
        return service_map.get_api_map_locations(session);
    }

    @Autowired
    private Service_RabbitCareMessageQueue queue;

    @Operation(summary = "message queue for locations (send location here from another app)")
    @PostMapping("/api/map/locations/send")
    public ResponseEntity<String> receiveMessage(@RequestBody DTO_Get_Location location) {
        queue.addMessage(location);
        return ResponseEntity.ok("Message received.");
    }
}
