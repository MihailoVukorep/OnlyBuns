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
    private Service_Email serviceEmail;

    @GetMapping("/api/verify")
    public ResponseEntity<String> api_verify(@RequestParam("token") String token) { return serviceEmail.api_verify(token); }
}
