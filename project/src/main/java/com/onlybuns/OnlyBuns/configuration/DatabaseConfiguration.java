package com.onlybuns.OnlyBuns.configuration;

import com.onlybuns.OnlyBuns.model.Account;
import com.onlybuns.OnlyBuns.model.AccountRole;
import com.onlybuns.OnlyBuns.repository.Repository_Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.List;

@Configuration
public class DatabaseConfiguration {

    @Autowired
    private Repository_Account repositoryAccount;

    public void CreateAccount(String email, String userName, String password, String firstName, String lastName, String address, String avatar, String bio, AccountRole accountRole) {

        Account account = new Account(
                email,
                userName,
                password,
                firstName,
                lastName,
                address,
                avatar,
                bio,
                AccountRole.USER
        );

        repositoryAccount.save(account);
    }


    @Bean
    public boolean instantiate() {



        CreateAccount(
                "pera@gmail.com",
                "rope",
                "123",
                "pera",
                "peric",
                "bulevar 22",
                "/avatars/default.jpg",
                "veoma ozbiljan lik",
                AccountRole.USER
        );

        CreateAccount(
                "bibi@gmail.com",
                "bibi",
                "123",
                "bibi",
                "patak",
                "sutjeska 13",
                "/avatars/default.jpg",
                "veoma kul lik",
                AccountRole.USER
        );

        CreateAccount(
                "bigboss@gmail.com",
                "snake",
                "123",
                "Big",
                "Boss",
                "motherbase",
                "/avatars/default.jpg",
                "big scary admin guy",
                AccountRole.ADMIN
        );


        List<Account> accounts = repositoryAccount.findAll();

        for (Account s : accounts) {
            System.out.println(s.toString());
        }

        return true;
    }
}
