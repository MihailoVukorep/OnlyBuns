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

    @Bean
    public boolean instantiate() {

        Account account = new Account(
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

        repositoryAccount.save(account);

        List<Account> accounts = repositoryAccount.findAll();

        for (Account s : accounts) {
            System.out.println(s.toString());
        }

        return true;
    }
}
