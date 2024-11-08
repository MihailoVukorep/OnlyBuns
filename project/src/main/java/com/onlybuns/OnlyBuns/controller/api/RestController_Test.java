package com.onlybuns.OnlyBuns.controller.api;
import com.onlybuns.OnlyBuns.service.Service_Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestController_Test {

    @Autowired
    private Service_Test service_test;

    @GetMapping("/api/test")
    public ResponseEntity<String> get_api_test() { return service_test.get_api_test();}
}
