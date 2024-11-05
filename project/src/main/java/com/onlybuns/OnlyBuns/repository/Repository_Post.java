package com.onlybuns.OnlyBuns.repository;
import com.onlybuns.OnlyBuns.model.Account;
import org.springframework.data.domain.Sort;
import com.onlybuns.OnlyBuns.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface Repository_Post extends JpaRepository<Post, Integer> {
    List<Post> findAll();
    List<Post> findAll(Sort sort);
    List<Post> findAllByAccount(Account account);
}
