package com.onlybuns.OnlyBuns.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Controller_Account {

    @GetMapping("/login")
    public String login() { return "login.html"; }

    @GetMapping("/register")
    public String register() { return "register.html"; }

    @GetMapping("/logout")
    public String logout() { return "logout.html"; }

    @GetMapping("/accounts")
    public String accounts() { return "accounts.html"; }
}
