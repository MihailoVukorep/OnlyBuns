package com.onlybuns.OnlyBuns.controller.web;

import com.onlybuns.OnlyBuns.dto.DTO_Get_Account;
import com.onlybuns.OnlyBuns.model.Account;
import com.onlybuns.OnlyBuns.service.Service_Account;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class Controller_Account {

    @Autowired
    private Service_Account service_account;

    @GetMapping("/login")
    public String login(HttpSession session, Model model) {
        return "login.html";
    }

    @GetMapping("/register")
    public String register(HttpSession session, Model model) {
        return "register.html";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, Model model) {
        service_account.post_api_logout(session);
        return "redirect:/";
    }

    // ACCOUNT STUFF
    @GetMapping("/accounts/{id}")
    public String accounts_id(HttpSession session, Model model, HttpServletRequest request, @PathVariable(name = "id") Long id) {
        model.addAttribute("account", service_account.get_api_accounts_id(session, id).getBody());
        model.addAttribute("request", request);
        return "account.html";
    }
    @GetMapping("/accounts/{id}/followers")
    public String accounts_id_followers(HttpSession session, Model model, @PathVariable(name = "id") Long id) {
        ResponseEntity<List<DTO_Get_Account>> response = service_account.get_api_accounts_id_followers(id);
        if (response.getStatusCode() == HttpStatus.NOT_FOUND) { return "error/404.html"; }
        model.addAttribute("accounts", response.getBody());
        return "accounts_raw.html";
    }
    @GetMapping("/accounts/{id}/following")
    public String accounts_id_following(HttpSession session, Model model, @PathVariable(name = "id") Long id) {
        ResponseEntity<List<DTO_Get_Account>> response = service_account.get_api_accounts_id_following(id);
        if (response.getStatusCode() == HttpStatus.NOT_FOUND) { return "error/404.html"; }
        model.addAttribute("accounts", response.getBody());
        return "accounts_raw.html";
    }

    // USER
    @GetMapping("/user")
    public String user(HttpSession session, Model model, HttpServletRequest request) {
        Account user = (Account) session.getAttribute("user");
        if (user == null) { return "error/401.html"; }
        return accounts_id(session, model, request, user.getId());
    }
    @GetMapping("/user/followers")
    public String user_followers(HttpSession session, Model model) {
        Account user = (Account) session.getAttribute("user");
        if (user == null) { return "error/401.html"; }
        return accounts_id_followers(session, model, user.getId());
    }
    @GetMapping("/user/following")
    public String user_following(HttpSession session, Model model) {
        Account user = (Account) session.getAttribute("user");
        if (user == null) { return "error/401.html"; }
        return accounts_id_following(session, model, user.getId());
    }

    // TODO: UPDATE ACCOUNT
}
