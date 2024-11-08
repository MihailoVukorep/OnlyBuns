package com.onlybuns.OnlyBuns.repository;
import com.onlybuns.OnlyBuns.model.Account;
import com.onlybuns.OnlyBuns.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface Repository_Like extends JpaRepository<Like, Long> {
    List<Like> findAll();
    List<Like> findAllByAccount(Account account);
    Optional<Like> findByAccountIdAndPostId(Long accountId, Long postId);
}
