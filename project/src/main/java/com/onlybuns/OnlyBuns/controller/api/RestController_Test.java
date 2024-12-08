package com.onlybuns.OnlyBuns.controller.api;

import com.onlybuns.OnlyBuns.service.Service_Test;
import com.onlybuns.OnlyBuns.service.Service_Test_Likes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestController_Test {

    @Autowired
    private Service_Test service_test;

    @Autowired
    private Service_Test_Likes service_test_likes;

    @GetMapping("/api/test")
    public ResponseEntity<String> get_api_test() {
        return service_test.get_api_test();
    }

    /*@GetMapping("/api/test/conccurent")
    public ResponseEntity<String> get_api_conccurent_likes() {
        try {
            return service_test_likes.get_api_conccurent_likes();
        } catch (Throwable t) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Došlo je do greške: " + t.getMessage());
        }
    }*/
}
