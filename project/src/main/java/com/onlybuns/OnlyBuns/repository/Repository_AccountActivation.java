package com.onlybuns.OnlyBuns.repository;
import com.onlybuns.OnlyBuns.model.Account;
import com.onlybuns.OnlyBuns.model.AccountActivation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface Repository_AccountActivation extends JpaRepository<AccountActivation, Long> {
    Optional<AccountActivation> findByToken(String token);
    Optional<AccountActivation> findByAccount(Account account);
    boolean existsByToken(String token);
}
