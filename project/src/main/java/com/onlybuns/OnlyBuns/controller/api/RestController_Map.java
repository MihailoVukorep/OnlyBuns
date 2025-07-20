package com.onlybuns.OnlyBuns.controller.api;

import com.onlybuns.OnlyBuns.dto.DTO_Get_Location;
import com.onlybuns.OnlyBuns.dto.DTO_Get_Locations;
import com.onlybuns.OnlyBuns.service.Service_Map;
import com.onlybuns.OnlyBuns.service.Service_RabbitCareMessageQueue;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestController_Map {

    @Autowired
    private Service_Map service_map;

    @Operation(
            summary = "Get all map locations",
            description = "Returns all locations relevant to the map, including posts, shelters, veterinarians, and other messages. " +
                    "Requires user authentication.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of locations returned successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized: User must be logged in")
            }
    )
    @GetMapping("/api/map/locations")
    public ResponseEntity<DTO_Get_Locations> get_api_map_locations(HttpSession session) {
        return service_map.get_api_map_locations(session);
    }

    @Autowired
    private Service_RabbitCareMessageQueue queue;

    @Operation(
            summary = "Message queue for locations",
            description = "Receive location messages posted from another application and add them to the message queue.",
            requestBody = @RequestBody(
                    description = "Location data to be added to the queue",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = DTO_Get_Location.class)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Message received successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid request body")
            }
    )
    @PostMapping("/api/map/locations/send")
    public ResponseEntity<String> receiveMessage(@RequestBody DTO_Get_Location location) {
        queue.addMessage(location);
        return ResponseEntity.ok("Message received.");
    }
}
