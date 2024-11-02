package com.onlybuns.OnlyBuns.repository;

import com.onlybuns.OnlyBuns.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;

public interface Repository_Account extends JpaRepository<Account, Integer> {

    public List<Account> findAll();

    public Optional<Account> findByEmail(String email);
    public Optional<Account> findByUserName(String userName);
}
