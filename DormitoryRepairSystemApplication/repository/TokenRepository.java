package com.wut.dormrepair.repository;

import com.wut.dormrepair.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository
        extends JpaRepository<Token, Long> {

    boolean existsByToken(String token);
}
