package com.onlybuns.OnlyBuns.controller.web;

import com.onlybuns.OnlyBuns.dto.DTO_Get_Message;
import com.onlybuns.OnlyBuns.dto.DTO_Post_Message;
import com.onlybuns.OnlyBuns.model.Account;
import com.onlybuns.OnlyBuns.model.Chat;
import com.onlybuns.OnlyBuns.model.Message;
import com.onlybuns.OnlyBuns.repository.Repository_Chat;
import com.onlybuns.OnlyBuns.repository.Repository_Message;
import com.onlybuns.OnlyBuns.service.Service_Chat;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class Controller_Chat {

    @Autowired
    private Service_Chat service_chat;

    @GetMapping("/chats")
    public String chats(HttpSession session, Model model) {
        Account user = (Account) session.getAttribute("user");
        if (user == null) { return "error/401.html"; }

        // set chats
        model.addAttribute("chats", service_chat.get_api_chats(session).getBody());
        return "chats";
    }

    @GetMapping("/chats/{id}")
    public String chats_id(HttpSession session, Model model, @PathVariable(name = "id") Long id) {
        Account user = (Account) session.getAttribute("user");
        if (user == null) { return "error/401.html"; }

        // set chats
        model.addAttribute("user_userName", user.getUserName());
        model.addAttribute("chats", service_chat.get_api_chats(session).getBody());
        model.addAttribute("chat_id", id);
        model.addAttribute("messages", service_chat.get_api_chats_id(session, id).getBody());
        return "chats";
    }

    @GetMapping("/accounts/{id}/chat")
    public String accounts_id_chat(HttpSession session, Model model, @PathVariable(name = "id") Long id) {
        Account user = (Account) session.getAttribute("user");
        if (user == null) { return "error/401.html"; }

        service_chat.get_api_accounts_id_chat(session, id);
        return "redirect:/chats";
    }

    @PostMapping("/chats/{id}")
    public String post_chats_id(HttpSession session, Model model, @PathVariable(name = "id") Long id, @RequestParam(required = true) String text) {

        Account user = (Account) session.getAttribute("user");
        if (user == null) { return "error/401.html"; }

        service_chat.post_api_chats_id(session, id, text);
        return chats_id(session, model, id);
    }

    @Autowired
    private Repository_Message repository_message;

    @Autowired
    private Repository_Chat repository_chat;

    @Autowired
    private HttpSession httpSession;  // Inject the HttpSession

    @MessageMapping("/send/{chatId}")
    @SendTo("/topic/messages/{chatId}")
    public DTO_Get_Message sendMessage(@DestinationVariable Long chatId, DTO_Post_Message dto_post_message) {
        ResponseEntity<DTO_Get_Message> response = service_chat.post_api_chats_id(dto_post_message.getUserName(), chatId, dto_post_message.getContent());
        return response.getBody();
    }
}
