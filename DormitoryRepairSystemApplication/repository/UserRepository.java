package com.wut.dormrepair.repository;

import com.wut.dormrepair.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    //Spring Security Login
    Optional<User> findByUsername(String username);
}
