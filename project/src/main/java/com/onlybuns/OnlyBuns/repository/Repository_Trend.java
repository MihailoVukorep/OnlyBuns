package com.onlybuns.OnlyBuns.repository;

import com.onlybuns.OnlyBuns.model.Trend;
import org.springframework.data.jpa.repository.JpaRepository;


public interface Repository_Trend extends JpaRepository<Trend, Long> {
    Trend findFirstByOrderByLastUpdatedDesc();
}

