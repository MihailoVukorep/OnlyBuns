package com.onlybuns.OnlyBuns.controller.api;

import com.onlybuns.OnlyBuns.service.Service_Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestController_Email {

    @Autowired
    private Service_Email service_email;

    @io.swagger.v3.oas.annotations.Operation(
            summary = "Verify user email",
            description = "Endpoint for verifying user email addresses using a token sent via registration email. " +
                    "Activates the user account if the token is valid and not already used.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Account verified successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid or expired verification token, or account already verified")
            }
    )
    @GetMapping("/api/verify")
    public ResponseEntity<String> get_api_verify(@RequestParam("token") String token) {
        return service_email.get_api_verify(token);
    }
}
