package com.onlybuns.OnlyBuns.controller.api;

import com.onlybuns.OnlyBuns.dto.DTO_Post_AccountLogin;
import com.onlybuns.OnlyBuns.dto.DTO_Post_AccountRegister;
import com.onlybuns.OnlyBuns.dto.DTO_View_Account;
import com.onlybuns.OnlyBuns.service.Service_Account;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RestController_Account {

    @Autowired
    private Service_Account serviceAccount;

    @GetMapping(value = "/api/accounts")
    public ResponseEntity<List<DTO_View_Account>> api_accounts() { return serviceAccount.api_accounts(); }

    @GetMapping("/api/myaccount")
    public ResponseEntity<DTO_View_Account> api_myaccount(HttpSession session) { return serviceAccount.api_myaccount(session); }

    @GetMapping("/api/accounts/{id}")
    public ResponseEntity<DTO_View_Account> api_account_id(@PathVariable(name = "id") Integer id, HttpSession session) {
        return serviceAccount.api_accounts_id(id, session);
    }

    @PostMapping("/api/login")
    public ResponseEntity<String> api_login(@RequestBody DTO_Post_AccountLogin dto_post_accountLogin, HttpServletRequest request, HttpSession session){
        return serviceAccount.api_login(dto_post_accountLogin, request, session);
    }

    @PostMapping("/api/register")
    public ResponseEntity<String> api_register(@RequestBody DTO_Post_AccountRegister dto_post_accountRegister, HttpSession session) {
        return serviceAccount.api_register(dto_post_accountRegister, session);
    }

    @PostMapping("/api/logout")
    public ResponseEntity<String> api_logout(HttpSession session) { return serviceAccount.logout(session); }
}
