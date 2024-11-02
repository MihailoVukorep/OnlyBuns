package com.onlybuns.OnlyBuns.service;


import com.onlybuns.OnlyBuns.dto.DTO_Post_AccountRegister;
import com.onlybuns.OnlyBuns.dto.DTO_View_Account;
import com.onlybuns.OnlyBuns.model.Account;
import com.onlybuns.OnlyBuns.repository.Repository_Account;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class Service_Account {

    @Autowired
    private Repository_Account accountRepository;

    public ResponseEntity<List<DTO_View_Account>> api_accounts() {
        List<Account> accounts = accountRepository.findAll();
        List<DTO_View_Account> accountDTOS = new ArrayList<>();
        for (Account account : accounts) { accountDTOS.add(new DTO_View_Account(account)); }
        return new ResponseEntity<>(accountDTOS, HttpStatus.OK);
    }

    public ResponseEntity<DTO_View_Account> api_myaccount(HttpSession session){
        Account account = (Account) session.getAttribute("account");
        if (account == null) { return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED); }

        Optional<Account> foundAccount = accountRepository.findById(account.getId());
        if (foundAccount.isEmpty()) { return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); }

        return new ResponseEntity<>(new DTO_View_Account(foundAccount.get()), HttpStatus.OK);
    }

    public ResponseEntity<String> api_register(@RequestBody DTO_Post_AccountRegister accountRequest, HttpSession session) {
        Account user = (Account) session.getAttribute("user");
        if (user != null) { return new ResponseEntity<>("already logged in", HttpStatus.BAD_REQUEST); }

        Optional<Account> foundAccount = accountRepository.findByEmail(accountRequest.getEmail());
        if (!foundAccount.isEmpty()) { return new ResponseEntity<>("email exists: " + accountRequest.getEmail(), HttpStatus.CONFLICT); }

        foundAccount = accountRepository.findByUserName(accountRequest.getUsername());
        if (!foundAccount.isEmpty()) { return new ResponseEntity<>("username exists: " + accountRequest.getUsername(), HttpStatus.CONFLICT); }


        session.setAttribute("account", foundAccount);
        return new ResponseEntity<>("registered", HttpStatus.OK);
    }

    public ResponseEntity<String> logout(HttpSession session) {
        Account user = (Account) session.getAttribute("user");
        if (user == null) { return new ResponseEntity<>("already logged out", HttpStatus.BAD_REQUEST); }

        session.invalidate();
        return new ResponseEntity<>("logged out: " + user.getUserName(), HttpStatus.OK);
    }
}
