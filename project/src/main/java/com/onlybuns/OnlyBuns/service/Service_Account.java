package com.onlybuns.OnlyBuns.service;


import com.onlybuns.OnlyBuns.dto.DTO_Post_AccountLogin;
import com.onlybuns.OnlyBuns.dto.DTO_Post_AccountRegister;
import com.onlybuns.OnlyBuns.dto.DTO_View_Account;
import com.onlybuns.OnlyBuns.model.Account;
import com.onlybuns.OnlyBuns.model.AccountRole;
import com.onlybuns.OnlyBuns.repository.Repository_Account;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

    public ResponseEntity<DTO_View_Account> api_accounts_id(@PathVariable(name = "id") Integer id, HttpSession session) {
        Optional<Account> foundAccount = accountRepository.findById(id);
        if (foundAccount.isEmpty()) { return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); }

        return new ResponseEntity<>(new DTO_View_Account(foundAccount.get()), HttpStatus.OK);
    }

    public ResponseEntity<DTO_View_Account> api_myaccount(HttpSession session){
        Account sessionAccount = (Account) session.getAttribute("account");
        if (sessionAccount == null) { return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED); }

        Optional<Account> foundAccount = accountRepository.findById(sessionAccount.getId());
        if (foundAccount.isEmpty()) { return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); }

        return new ResponseEntity<>(new DTO_View_Account(foundAccount.get()), HttpStatus.OK);
    }

    public ResponseEntity<String> api_login(@RequestBody DTO_Post_AccountLogin dto_post_accountLogin, HttpSession session){
        Account sessionAccount = (Account) session.getAttribute("account");
        if (sessionAccount != null) { return new ResponseEntity<>("already logged in", HttpStatus.BAD_REQUEST); }

        if (dto_post_accountLogin.getEmail().isEmpty() || dto_post_accountLogin.getPassword().isEmpty()) { return new ResponseEntity<>("invalid login data", HttpStatus.BAD_REQUEST); }

        Optional<Account> foundAccount = accountRepository.findByEmail(dto_post_accountLogin.getEmail());
        if (foundAccount.isEmpty()) {
            foundAccount = accountRepository.findByUserName(dto_post_accountLogin.getEmail());
        }

        if (foundAccount.isEmpty()) {
            return new ResponseEntity<>("account not found", HttpStatus.NOT_FOUND);
        }

        Account account = foundAccount.get();

        if (!account.getPassword().equals(dto_post_accountLogin.getPassword())) { return new ResponseEntity<>("wrong password", HttpStatus.UNAUTHORIZED); }

        session.setAttribute("account", account);
        return new ResponseEntity<>("logged in as: " + account.getUserName(), HttpStatus.OK);
    }

    public ResponseEntity<String> api_register(@RequestBody DTO_Post_AccountRegister dto_post_accountRegister, HttpSession session) {
        Account sessionAccount = (Account) session.getAttribute("account");
        if (sessionAccount != null) { return new ResponseEntity<>("already logged in", HttpStatus.BAD_REQUEST); }

        Optional<Account> foundAccount = accountRepository.findByEmail(dto_post_accountRegister.getEmail());
        if (!foundAccount.isEmpty()) { return new ResponseEntity<>("email exists: " + dto_post_accountRegister.getEmail(), HttpStatus.CONFLICT); }

        foundAccount = accountRepository.findByUserName(dto_post_accountRegister.getUserName());
        if (!foundAccount.isEmpty()) { return new ResponseEntity<>("username exists: " + dto_post_accountRegister.getUserName(), HttpStatus.CONFLICT); }

        Account newAccount = new Account(
                dto_post_accountRegister.getEmail(),
                dto_post_accountRegister.getUserName(),
                dto_post_accountRegister.getPassword(),
                dto_post_accountRegister.getFirstName(),
                dto_post_accountRegister.getLastName(),
                dto_post_accountRegister.getAddress(),
                "/avatars/default.jpg",
                "...",
                AccountRole.USER
        );
        accountRepository.save(newAccount);
        session.setAttribute("account", newAccount);
        return new ResponseEntity<>("registered: " + newAccount, HttpStatus.OK);
    }

    public ResponseEntity<String> logout(HttpSession session) {
        Account sessionAccount = (Account) session.getAttribute("account");
        if (sessionAccount == null) { return new ResponseEntity<>("already logged out", HttpStatus.BAD_REQUEST); }

        session.invalidate();
        return new ResponseEntity<>("logged out: " + sessionAccount.getUserName(), HttpStatus.OK);
    }
}
