package com.onlybuns.OnlyBuns.controller.web;

import com.onlybuns.OnlyBuns.model.Account;
import com.onlybuns.OnlyBuns.service.Service_Chat;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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

    @GetMapping("/accounts/{id}/chat")
    public String accounts_id_chat(HttpSession session, Model model, @PathVariable(name = "id") Long id) {
        service_chat.get_api_accounts_id_chat(session, id);
        return "redirect:/chats";
    }
}
