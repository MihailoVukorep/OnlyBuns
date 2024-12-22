package com.onlybuns.OnlyBuns.repository;

import com.onlybuns.OnlyBuns.model.Account;
import com.onlybuns.OnlyBuns.model.Like;
import com.onlybuns.OnlyBuns.model.Post;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface Repository_Like extends JpaRepository<Like, Long> {
    List<Like> findAll();

    List<Like> findAllByAccount(Account account);

    Optional<Like> findByAccountIdAndPostId(Long accountId, Long postId);

    List<Like> findByPostIdInAndAccount(List<Long> postIds, Account account);

    boolean existsByPostAndAccount(Post post, Account account);
    @Transactional
    @Modifying
    @Query("DELETE FROM Like l WHERE l.post.account.id = :accountId")
    void deleteByPostAccountId(@Param("accountId") Long accountId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Like l WHERE l.account.id = :accountId")
    void deleteByAccountId(@Param("accountId") Long accountId);

}
