package com.onlybuns.OnlyBuns.controller.api;

import com.onlybuns.OnlyBuns.dto.DTO_Get_Chat;
import com.onlybuns.OnlyBuns.dto.DTO_Get_Message;
import com.onlybuns.OnlyBuns.service.Service_Chat;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RestController_Chat {

    @Autowired
    private Service_Chat service_chat;


    @Operation(summary = "current user's chats")
    @GetMapping("/api/chats")
    public ResponseEntity<List<DTO_Get_Chat>> get_api_chats(HttpSession session) {
        return service_chat.get_api_chats(session);
    }

    // create chat
    @Operation(summary = "create chat")
    @GetMapping("/api/accounts/{id}/chat")
    public ResponseEntity<String> get_api_accounts_id_chat(HttpSession session, @PathVariable(name = "id") Long id) {
        return service_chat.get_api_accounts_id_chat(session, id);
    }

    // get chat info
    @Operation(summary = "get chat info by id")
    @GetMapping("/api/chats/{id}")
    public ResponseEntity<DTO_Get_Chat> get_api_chats_id(HttpSession session, @PathVariable(name = "id") Long id) {
        return service_chat.get_api_chats_id(session, id);
    }

    // get messages
    @Operation(summary = "get chat's messages")
    @GetMapping("/api/chats/{id}/messages")
    public ResponseEntity<List<DTO_Get_Message>> get_api_chats_id_messages(HttpSession session, @PathVariable(name = "id") Long id) {
        return service_chat.get_api_chats_id_messages(session, id);
    }

    // send message to chat
    @Operation(summary = "send message to chat")
    @PostMapping("/api/chats/{id}/messages")
    public ResponseEntity<DTO_Get_Message> post_api_chats_id_messages(HttpSession session, @PathVariable(name = "id") Long id, @RequestParam(required = true) String text) {
        return service_chat.post_api_chats_id_messages(session, id, text);
    }

    // add person to chat
    @Operation(summary = "add person to chat")
    @GetMapping("/api/chats/{id}/add/{account_id}")
    public ResponseEntity<String> get_api_chats_id_add_id(HttpSession session, @PathVariable(name = "id") Long id, @PathVariable(name = "account_id") Long account_id) {
        return service_chat.get_api_chats_id_add_id(session, id, account_id);
    }

    // remove person from chat
    @Operation(summary = "remove person to chat")
    @GetMapping("/api/chats/{id}/remove/{account_id}")
    public ResponseEntity<String> get_api_chats_id_remove_id(HttpSession session, @PathVariable(name = "id") Long id, @PathVariable(name = "account_id") Long account_id) {
        return service_chat.get_api_chats_id_remove_id(session, id, account_id);
    }

    @GetMapping("/api/chats/{id}/leave")
    @Operation(summary = "leave chat")
    public ResponseEntity<String> get_api_chats_id_leave(HttpSession session, @PathVariable(name = "id") Long id) {
        return service_chat.get_api_chats_id_leave(session, id);
    }
}
