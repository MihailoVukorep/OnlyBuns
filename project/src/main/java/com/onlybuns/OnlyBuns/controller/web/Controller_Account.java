package com.onlybuns.OnlyBuns.controller.web;

import com.onlybuns.OnlyBuns.model.Account;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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

    @GetMapping("/myaccount")
    public String myaccount(HttpSession session, Model model) {
        Account user = (Account) session.getAttribute("account");
        if (user == null) { return "error.html"; }

        model.addAttribute("account_id", user.getId());
        return "account.html";
    }

    @GetMapping("/accounts/{id}")
    public String users_id(@PathVariable(name = "id") Long id, Model model) {
        model.addAttribute("account_id", id);
        return "account.html";
    }
}
