package com.onlybuns.OnlyBuns.controller.api;

import com.onlybuns.OnlyBuns.service.Service_Chat;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestController_Chat {

    @Autowired
    private Service_Chat service_chat;

    @GetMapping("/api/accounts/{id}/chat")
    public ResponseEntity<String> get_api_accounts_id_message(HttpSession session, @PathVariable(name = "id") Long id) {
        return service_chat.get_api_accounts_id_chat(session, id);
    }
}
