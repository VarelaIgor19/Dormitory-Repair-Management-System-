package com.wut.dormrepair.entity;

import jakarta.persistence.*;

@Entity
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1000, nullable = false)
    private String token;

    public Token() {}

    public Token(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
