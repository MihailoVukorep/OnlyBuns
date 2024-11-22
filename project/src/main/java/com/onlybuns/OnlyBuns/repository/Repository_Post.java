package com.onlybuns.OnlyBuns.repository;

import com.onlybuns.OnlyBuns.model.Account;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import com.onlybuns.OnlyBuns.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface Repository_Post extends JpaRepository<Post, Long> {
    List<Post> findAll();

    List<Post> findAll(Sort sort);

    List<Post> findByParentPostIsNull();

    List<Post> findByParentPostIsNull(Sort sort);

    List<Post> findAllByAccount(Account account);

    List<Post> findAllByAccount(Account account, Sort sort);

    List<Post> findAll(Sort sort, PageRequest pageRequest);
}
