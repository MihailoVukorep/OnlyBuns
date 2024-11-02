package com.onlybuns.OnlyBuns.controller;

import com.onlybuns.OnlyBuns.dto.AccountDTO;
import com.onlybuns.OnlyBuns.model.Account;
import com.onlybuns.OnlyBuns.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping(value = "api/accounts/all")
    public ResponseEntity<List<AccountDTO>> getAllAccounts() {

        List<Account> accounts = accountService.findAll();

        // convert students to DTOs
        List<AccountDTO> accountDTOS = new ArrayList<>();
        for (Account account : accounts) {
            accountDTOS.add(new AccountDTO(account));
        }

        return new ResponseEntity<>(accountDTOS, HttpStatus.OK);
    }
}
