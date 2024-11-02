package com.onlybuns.OnlyBuns.repository;

import com.onlybuns.OnlyBuns.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface Repository_Account extends JpaRepository<Account, Integer> {

    public List<Account> findAll();
}
