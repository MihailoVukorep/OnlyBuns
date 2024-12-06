package com.onlybuns.OnlyBuns.controller.api;

import com.onlybuns.OnlyBuns.dto.*;
import com.onlybuns.OnlyBuns.service.Service_Account;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RestController_Account {

    @Autowired
    private Service_Account service_account;

    @GetMapping("/api/user")
    public ResponseEntity<DTO_Get_Account> get_api_user(HttpSession session) {
        return service_account.get_api_user(session);
    }

    @GetMapping("/api/accounts/{id}")
    public ResponseEntity<DTO_Get_Account> get_api_accounts_id(HttpSession session, @PathVariable(name = "id") Long id) {
        return service_account.get_api_accounts_id(session, id);
    }

    @GetMapping("/api/accounts/{id}/likes")
    public ResponseEntity<List<DTO_Get_Like>> get_api_accounts_id_likes(@PathVariable(name = "id") Long id) {
        return service_account.get_api_accounts_id_likes(id);
    }

    @PostMapping("/api/login")
    public ResponseEntity<String> post_api_login(@RequestBody DTO_Post_AccountLogin dto_post_accountLogin, HttpServletRequest request, HttpSession session) {
        return service_account.post_api_login(dto_post_accountLogin, request, session);
    }

    @PostMapping("/api/register")
    public ResponseEntity<String> post_api_register(@RequestBody DTO_Post_AccountRegister dto_post_accountRegister, HttpSession session) {
        return service_account.post_api_register(dto_post_accountRegister, session);
    }

    @PostMapping("/api/logout")
    public ResponseEntity<String> post_api_logout(HttpSession session) {
        return service_account.post_api_logout(session);
    }

    @PostMapping("/api/accounts/{id}/follow")
    public ResponseEntity<String> get_api_accounts_id_follow(HttpSession session, @PathVariable(name = "id") Long id) {
        return service_account.post_api_accounts_id_follow(session, id);
    }

    @GetMapping("/api/accounts/{id}/followers")
    public ResponseEntity<List<DTO_Get_Account>> get_api_accounts_id_followers(@PathVariable(name = "id") Long id) {
        return service_account.get_api_accounts_id_followers(id);
    }

    @GetMapping("/api/accounts/{id}/following")
    public ResponseEntity<List<DTO_Get_Account>> get_api_accounts_id_following(@PathVariable(name = "id") Long id) {
        return service_account.get_api_accounts_id_following(id);
    }
}
