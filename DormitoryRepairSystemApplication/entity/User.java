package com.wut.dormrepair.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role; // STUDENT / REPAIR_STAFF / ADMIN

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createTime;

    public User() {}

    @PrePersist
    public void onCreate() {
        this.createTime = LocalDateTime.now();
    }
}