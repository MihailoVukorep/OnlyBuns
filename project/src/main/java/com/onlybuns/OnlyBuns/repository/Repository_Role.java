package com.onlybuns.OnlyBuns.repository;

import com.onlybuns.OnlyBuns.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface Repository_Role extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
