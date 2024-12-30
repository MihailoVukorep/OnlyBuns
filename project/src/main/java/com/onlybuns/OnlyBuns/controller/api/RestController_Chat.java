package com.onlybuns.OnlyBuns.controller.api;

import com.onlybuns.OnlyBuns.dto.DTO_Get_Chat;
import com.onlybuns.OnlyBuns.dto.DTO_Get_Message;
import com.onlybuns.OnlyBuns.service.Service_Chat;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RestController_Chat {

    @Autowired
    private Service_Chat service_chat;

    @GetMapping("/api/chats")
    public ResponseEntity<List<DTO_Get_Chat>> get_api_chats(HttpSession session) {
        return service_chat.get_api_chats(session);
    }

    // create chat
    @GetMapping("/api/accounts/{id}/chat")
    public ResponseEntity<String> get_api_accounts_id_chat(HttpSession session, @PathVariable(name = "id") Long id) {
        return service_chat.get_api_accounts_id_chat(session, id);
    }

    // get chat info
    @GetMapping("/api/chats/{id}")
    public ResponseEntity<DTO_Get_Chat> get_api_chats_id(HttpSession session, @PathVariable(name = "id") Long id) {
        return service_chat.get_api_chats_id(session, id);
    }

    // get messages
    @GetMapping("/api/chats/{id}/messages")
    public ResponseEntity<List<DTO_Get_Message>> get_api_chats_id_messages(HttpSession session, @PathVariable(name = "id") Long id) {
        return service_chat.get_api_chats_id_messages(session, id);
    }

    // send message to chat
    @PostMapping("/api/chats/{id}/messages")
    public ResponseEntity<DTO_Get_Message> post_api_chats_id_messages(HttpSession session, @PathVariable(name = "id") Long id, @RequestParam(required = true) String text) {
        return service_chat.post_api_chats_id_messages(session, id, text);
    }
}
