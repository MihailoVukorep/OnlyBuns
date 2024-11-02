package com.onlybuns.OnlyBuns.service;


import com.onlybuns.OnlyBuns.model.Account;
import com.onlybuns.OnlyBuns.repository.Repository_Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Service_Account {

    @Autowired
    private Repository_Account accountRepository;

    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    public Account save(Account account) {
        return accountRepository.save(account);
    }
}
