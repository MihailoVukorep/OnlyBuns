package com.onlybuns.OnlyBuns.repository;

import com.onlybuns.OnlyBuns.model.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.onlybuns.OnlyBuns.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface Repository_Post extends JpaRepository<Post, Long> {
    List<Post> findAll();
    List<Post> findAll(Sort sort);
    //List<Post> findByParentPostIsNull();
    Page<Post> findByParentPostIsNull(Pageable pageable);
    //List<Post> findAllByAccount(Account account);
    Page<Post> findAllByAccount(Account account, Pageable pageable);
}
