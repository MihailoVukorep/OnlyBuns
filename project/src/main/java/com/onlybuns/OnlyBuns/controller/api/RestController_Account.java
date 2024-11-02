package com.onlybuns.OnlyBuns.controller.api;

import com.onlybuns.OnlyBuns.dto.DTO_Post_AccountRegister;
import com.onlybuns.OnlyBuns.dto.DTO_View_Account;
import com.onlybuns.OnlyBuns.model.Account;
import com.onlybuns.OnlyBuns.service.Service_Account;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RestController_Account {

    @Autowired
    private Service_Account accountService;

    @GetMapping(value = "/api/accounts")
    public ResponseEntity<List<DTO_View_Account>> api_accounts() { return accountService.api_accounts(); }

    @GetMapping("/api/myaccount")
    public ResponseEntity<DTO_View_Account> myaccount(HttpSession session){ return accountService.api_myaccount(session); }

    @PostMapping("/api/register")
    public ResponseEntity<String> api_register(@RequestBody DTO_Post_AccountRegister accountRequest, HttpSession session) {
        return accountService.api_register(accountRequest, session);
    }

    @PostMapping("/api/logout")
    public ResponseEntity<String> logout(HttpSession session) { return accountService.logout(session); }
}
