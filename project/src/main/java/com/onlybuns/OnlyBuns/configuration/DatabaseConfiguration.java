package com.onlybuns.OnlyBuns.configuration;

import com.onlybuns.OnlyBuns.model.Account;
import com.onlybuns.OnlyBuns.model.AccountRole;
import com.onlybuns.OnlyBuns.model.Post;
import com.onlybuns.OnlyBuns.repository.Repository_Account;
import com.onlybuns.OnlyBuns.repository.Repository_Post;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.List;

@Configuration
public class DatabaseConfiguration {

    @Autowired
    private Repository_Account repositoryAccount;

    @Autowired
    private Repository_Post repositoryPost;

    public void printAll_accounts() {
        List<Account> accounts = repositoryAccount.findAll();
        for (Account i : accounts) { System.out.println(i.toString()); }
    }

    public void printAll_posts() {
        List<Post> posts = repositoryPost.findAll();
        for (Post i : posts) { System.out.println(i.toString()); }
    }

    public Account CreateAccount(String email, String userName, String password, String firstName, String lastName, String address, String avatar, String bio, AccountRole accountRole) {
        Account account = new Account(
                email,
                userName,
                password,
                firstName,
                lastName,
                address,
                avatar,
                bio,
                accountRole
        );
        repositoryAccount.save(account);
        return account;
    }

    public void CreatePost(String title, String text, Account account) {
        Post post = new Post(title, text, account);
        repositoryPost.save(post);
    }


    @Bean
    @Transactional
    public boolean instantiate() {

        CreateAccount(
                "pera@gmail.com",
                "rope",
                "123",
                "Pera",
                "Peric",
                "bulevar 22",
                "/avatars/default.jpg",
                "veoma ozbiljan lik",
                AccountRole.USER
        );

        Account account4post = CreateAccount(
                "ajzak@gmail.com",
                "ajzak",
                "123",
                "Ajs",
                "Nigrutin",
                "sutjeska 13",
                "/avatars/ajs.png",
                "gengsta lik",
                AccountRole.USER
        );

        CreatePost("Hello World 1", "Hello evereeehboodyyy!!!! 1 :^)", account4post);
        CreatePost("Hello World 2", "Hello evereeehboodyyy!!!! 2 :^)", account4post);
        CreatePost("Hello World 3", "Hello evereeehboodyyy!!!! 3 :^)", account4post);

        CreateAccount(
                "konstrakta@gmail.com",
                "konstrakta",
                "123",
                "Ana",
                "Djuric",
                "tu na keju",
                "/avatars/kons.png",
                "umetnica moze biti zdrava",
                AccountRole.USER
        );

        CreateAccount(
                "bigboss@gmail.com",
                "snake",
                "123",
                "Big",
                "Boss",
                "motherbase",
                "/avatars/bigboss.png",
                "big scary admin guy",
                AccountRole.ADMIN
        );

        printAll_accounts();
        printAll_posts();

        return true;
    }
}
