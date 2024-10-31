package com.onlybuns.OnlyBuns.repository;

import com.onlybuns.OnlyBuns.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Integer> {

    public List<Account> findAll();
}
