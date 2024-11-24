package com.onlybuns.OnlyBuns.controller.web;

import com.onlybuns.OnlyBuns.model.Account;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Controller_Map {

    @GetMapping("/map")
    public String map(HttpSession session, Model model) {
        Account user = (Account) session.getAttribute("user");
        if (user == null) { return "error/401.html"; }
        return "map";
    }
}