package com.onlybuns.OnlyBuns.controller.web;

import com.onlybuns.OnlyBuns.dto.DTO_Get_Account;
import com.onlybuns.OnlyBuns.model.Account;
import com.onlybuns.OnlyBuns.service.Service_Account;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Transactional
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
        return "logout.html";
    }

    @GetMapping("/user")
    public String user(HttpSession session, Model model) {
        Account user = (Account) session.getAttribute("user");
        if (user == null) {
            return "error/401.html";
        }

        model.addAttribute("account", new DTO_Get_Account(service_account.eager(user.getId())));
        return "account.html";
    }

    @GetMapping("/accounts/{id}")
    public String accounts_id(HttpSession session, Model model, HttpServletRequest request, @PathVariable(name = "id") Long id) {
        model.addAttribute("account", service_account.get_api_accounts_id_raw(id));
        model.addAttribute("request", request);
        return "account.html";
    }

    @GetMapping("/accounts/{id}/following")
    public String accounts_id_following(HttpSession session, Model model, @PathVariable(name = "id") Long id) {
        return "errors/501.html";
    }

    @GetMapping("/accounts/{id}/followers")
    public String accounts_id_followers(HttpSession session, Model model, @PathVariable(name = "id") Long id) {
        return "errors/501.html";
    }

    @GetMapping("/admin/manage")
    public String admin_manage(HttpSession session, Model model) {
        Account user = (Account) session.getAttribute("user");
        if (user == null || !user.isAdmin()) {
            return "error/403.html";
        }
        return "admin_manage.html";
    }

    @GetMapping("/admin/accounts")
    public String admin_accounts(HttpSession session,
                                Model model,
                                @RequestParam(required = false) String firstName,
                                @RequestParam(required = false) String lastName,
                                @RequestParam(required = false) String userName,
                                @RequestParam(required = false) String email,
                                @RequestParam(required = false) String address,
                                @RequestParam(required = false) Integer minPostCount,
                                @RequestParam(required = false) Integer maxPostCount,
                                @RequestParam(value = "sort", required = false) String sort) {
        Account user = (Account) session.getAttribute("user");
        if (user == null || !user.isAdmin()) {
            return "error/403.html";
        }

        // Fetch filtered and sorted accounts
        model.addAttribute("accounts", service_account.getFilteredAndSortedAccounts(session, firstName, lastName, userName, email, address, minPostCount, maxPostCount, sort));
        model.addAttribute("currentSort", sort);
        return "admin_accounts.html";
    }


    // TODO: UPDATE ACCOUNT
}
