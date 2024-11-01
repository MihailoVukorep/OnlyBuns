package com.onlybuns.OnlyBuns.configuration;

import com.onlybuns.OnlyBuns.model.Account;
import com.onlybuns.OnlyBuns.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DatabaseConfiguration {

    @Autowired
    private AccountRepository repositoryAccount;

    @Bean
    public boolean instantiate() {

        Account account = new Account("peraperic", "pera", "peric");

        repositoryAccount.save(account);

        List<Account> accounts = repositoryAccount.findAll();

        for (Account s : accounts) {
            System.out.println(s.toString());
        }

        return true;
    }
}
