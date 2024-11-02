package com.onlybuns.OnlyBuns.controller.api;

import com.onlybuns.OnlyBuns.dto.DTO_Post_Account;
import com.onlybuns.OnlyBuns.dto.DTO_Post_AccountLogin;
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

import java.util.ArrayList;
import java.util.List;

@RestController
public class RestController_Account {

    @Autowired
    private Service_Account accountService;

    @GetMapping(value = "api/accounts")
    public ResponseEntity<List<DTO_Post_Account>> getAllAccounts() {
        List<Account> accounts = accountService.findAll();
        List<DTO_Post_Account> accountDTOS = new ArrayList<>();
        for (Account account : accounts) { accountDTOS.add(new DTO_Post_Account(account)); }
        return new ResponseEntity<>(accountDTOS, HttpStatus.OK);
    }

}
