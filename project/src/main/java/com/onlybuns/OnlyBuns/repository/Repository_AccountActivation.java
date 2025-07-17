package com.onlybuns.OnlyBuns.repository;

import com.onlybuns.OnlyBuns.model.Account;
import com.onlybuns.OnlyBuns.model.AccountActivation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface Repository_AccountActivation extends JpaRepository<AccountActivation, Long> {
    Optional<AccountActivation> findByToken(String token);

    Optional<AccountActivation> findByAccount(Account account);

    boolean existsByToken(String token);

    void deleteAccountActivationByAccount_Id(Long id);

    @Modifying
    @Query("DELETE FROM AccountActivation aa WHERE aa.account = :account")
    void deleteByAccount(@Param("account") Account account);

}
