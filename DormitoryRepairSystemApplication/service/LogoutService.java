package com.wut.dormrepair.service;

import com.wut.dormrepair.entity.Token;
import com.wut.dormrepair.repository.TokenRepository;
import org.springframework.stereotype.Service;

@Service
public class LogoutService {

    private final TokenRepository tokenRepository;

    public LogoutService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    // store token in blacklist
    public void listToken(String token) {
        tokenRepository.save(new Token(token));
    }

    // check if token already logged out
    public boolean islisted(String token) {
        return tokenRepository.existsByToken(token);
    }
}
