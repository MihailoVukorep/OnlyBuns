package com.onlybuns.OnlyBuns.service;


import com.onlybuns.OnlyBuns.dto.DTO_Post_AccountLogin;
import com.onlybuns.OnlyBuns.dto.DTO_Post_AccountRegister;
import com.onlybuns.OnlyBuns.dto.DTO_View_Account;
import com.onlybuns.OnlyBuns.model.Account;
import com.onlybuns.OnlyBuns.model.AccountRole;
import com.onlybuns.OnlyBuns.repository.Repository_Account;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class Service_Account {

    @Autowired
    private Repository_Account repositoryAccount;

    public ResponseEntity<List<DTO_View_Account>> api_accounts() {
        List<Account> accounts = repositoryAccount.findAll();
        List<DTO_View_Account> accountDTOS = new ArrayList<>();
        for (Account account : accounts) { accountDTOS.add(new DTO_View_Account(account)); }
        return new ResponseEntity<>(accountDTOS, HttpStatus.OK);
    }

    public ResponseEntity<DTO_View_Account> api_accounts_id(@PathVariable(name = "id") Integer id, HttpSession session) {
        Optional<Account> foundAccount = repositoryAccount.findById(id);
        if (foundAccount.isEmpty()) { return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); }

        return new ResponseEntity<>(new DTO_View_Account(foundAccount.get()), HttpStatus.OK);
    }

    public ResponseEntity<DTO_View_Account> api_myaccount(HttpSession session){
        Account sessionAccount = (Account) session.getAttribute("account");
        if (sessionAccount == null) { return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED); }

        Optional<Account> foundAccount = repositoryAccount.findById(sessionAccount.getId());
        if (foundAccount.isEmpty()) { return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); }

        return new ResponseEntity<>(new DTO_View_Account(foundAccount.get()), HttpStatus.OK);
    }

    public ResponseEntity<String> api_login(@RequestBody DTO_Post_AccountLogin dto_post_accountLogin, HttpSession session){
        Account sessionAccount = (Account) session.getAttribute("account");
        if (sessionAccount != null) { return new ResponseEntity<>("Already logged in.", HttpStatus.BAD_REQUEST); }

        if (dto_post_accountLogin.getEmail().isEmpty() || dto_post_accountLogin.getPassword().isEmpty()) { return new ResponseEntity<>("Invalid login data.", HttpStatus.BAD_REQUEST); }

        Optional<Account> foundAccount = repositoryAccount.findByEmail(dto_post_accountLogin.getEmail());
        if (foundAccount.isEmpty()) { foundAccount = repositoryAccount.findByUserName(dto_post_accountLogin.getEmail()); }

        if (foundAccount.isEmpty()) { return new ResponseEntity<>("Account not found.", HttpStatus.NOT_FOUND); }

        Account account = foundAccount.get();

        if (!account.getPassword().equals(dto_post_accountLogin.getPassword())) { return new ResponseEntity<>("Wrong password.", HttpStatus.UNAUTHORIZED); }

        session.setAttribute("account", account);
        return new ResponseEntity<>("Logged in as: " + account.getUserName(), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<String> api_register(@RequestBody DTO_Post_AccountRegister dto_post_accountRegister, HttpSession session) {
        Account sessionAccount = (Account) session.getAttribute("account");
        if (sessionAccount != null) { return new ResponseEntity<>("Already logged in.", HttpStatus.BAD_REQUEST); }

        String message = dto_post_accountRegister.validate();
        if (message != null) { return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST); }

        // Lock on email and username checking to prevent concurrent issues
        synchronized (this) {
            // Check if the email is already in use
            Optional<Account> foundAccount = repositoryAccount.findByEmail(dto_post_accountRegister.getEmail());
            if (foundAccount.isPresent()) {
                return new ResponseEntity<>("Email exists: " + dto_post_accountRegister.getEmail(), HttpStatus.CONFLICT);
            }

            // Check if the username is already in use
            foundAccount = repositoryAccount.findByUserName(dto_post_accountRegister.getUserName());
            if (foundAccount.isPresent()) {
                return new ResponseEntity<>("Username exists: " + dto_post_accountRegister.getUserName(), HttpStatus.CONFLICT);
            }
        }

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
        repositoryAccount.save(newAccount);
        session.setAttribute("account", newAccount);
        return new ResponseEntity<>("Registered.", HttpStatus.OK);
    }

    public ResponseEntity<String> logout(HttpSession session) {
        Account sessionAccount = (Account) session.getAttribute("account");
        if (sessionAccount == null) { return new ResponseEntity<>("Already logged out.", HttpStatus.BAD_REQUEST); }

        session.invalidate();
        return new ResponseEntity<>("Logged out: " + sessionAccount.getUserName(), HttpStatus.OK);
    }
}
