package com.onlybuns.OnlyBuns.controller.api;

import com.onlybuns.OnlyBuns.dto.*;
import com.onlybuns.OnlyBuns.model.Account;
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
    private Service_Account service_account;

    @GetMapping("/api/user")
    public ResponseEntity<DTO_Get_Account> get_api_user(HttpSession session) { return service_account.get_api_user(session); }

    @GetMapping("/api/accounts/{id}")
    public ResponseEntity<DTO_Get_Account> get_api_account_id(@PathVariable(name = "id") Long id) { return service_account.get_api_accounts_id(id); }

    @GetMapping("/api/accounts/{id}/posts")
    public ResponseEntity<List<DTO_Get_Post>> get_api_account_id_posts(@PathVariable(name = "id") Long id, HttpSession session) { return service_account.get_api_accounts_id_posts(id, session); }

    @GetMapping("/api/accounts/{id}/likes")
    public ResponseEntity<List<DTO_Get_Like>> get_api_account_id_likes(@PathVariable(name = "id") Long id) { return service_account.get_api_accounts_id_likes(id); }

    @PostMapping("/api/login")
    public ResponseEntity<String> get_api_login(@RequestBody DTO_Post_AccountLogin dto_post_accountLogin, HttpServletRequest request, HttpSession session){ return service_account.get_api_login(dto_post_accountLogin, request, session); }

    @PostMapping("/api/register")
    public ResponseEntity<String> get_api_register(@RequestBody DTO_Post_AccountRegister dto_post_accountRegister, HttpSession session) { return service_account.get_api_register(dto_post_accountRegister, session); }

    @PostMapping("/api/logout")
    public ResponseEntity<String> get_api_logout(HttpSession session) { return service_account.get_api_logout(session); }

    @GetMapping("/api/admin/accounts")
    public ResponseEntity<List<DTO_Get_Account>> get_api_admin_accounts(
            HttpSession session,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) Integer minPostCount,
            @RequestParam(required = false) Integer maxPostCount) {
        return service_account.get_api_admin_accounts(session, firstName, lastName, userName, email, address, minPostCount, maxPostCount);
    }
}
