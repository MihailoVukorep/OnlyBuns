package com.onlybuns.OnlyBuns.service;

import com.onlybuns.OnlyBuns.model.Account;
import com.onlybuns.OnlyBuns.model.AccountActivation;
import com.onlybuns.OnlyBuns.model.Post;
import com.onlybuns.OnlyBuns.repository.Repository_Account;
import com.onlybuns.OnlyBuns.repository.Repository_AccountActivation;
import com.onlybuns.OnlyBuns.repository.Repository_Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Service_Test {


    @Autowired
    private Repository_Account repository_account;

    @Autowired
    private Repository_Post repository_post;

    @Autowired
    private Repository_AccountActivation repository_accountActivation;

    public void printAll_accounts() {
        List<Account> accounts = repository_account.findAll();
        for (Account i : accounts) { System.out.println(i.toString()); }
    }

    public void printAll_posts() {
        List<Post> posts = repository_post.findAll();
        for (Post i : posts) { System.out.println(i.toString()); }
    }

    public void printAll_accountActivations() {
        List<AccountActivation> accountActivations = repository_accountActivation.findAll();
        for (AccountActivation i : accountActivations) { System.out.println(i.toString()); }
    }

    public ResponseEntity<String> api_test() {

        printAll_accounts();
        printAll_posts();
        printAll_accountActivations();

        return new ResponseEntity<>("testing", HttpStatus.OK);
    }
}
