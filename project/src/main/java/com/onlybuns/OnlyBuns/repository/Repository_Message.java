package com.onlybuns.OnlyBuns.repository;

import com.onlybuns.OnlyBuns.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Repository_Message extends JpaRepository<Message, Long> {}