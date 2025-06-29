package com.onlybuns.OnlyBuns.controller.api;

import com.onlybuns.OnlyBuns.service.Service_Email;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestController_Email {

    @Autowired
    private Service_Email service_email;

    @Operation(summary = "endpoint for verifying emails - link sent by registering")
    @GetMapping("/api/verify")
    public ResponseEntity<String> get_api_verify(@RequestParam("token") String token) {
        return service_email.get_api_verify(token);
    }
}
