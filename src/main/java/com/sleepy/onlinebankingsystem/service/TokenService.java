package com.sleepy.onlinebankingsystem.service;

import com.sleepy.onlinebankingsystem.model.entity.Token;

import java.util.Optional;

public interface TokenService {
    Token save(Token token) throws Exception;
    void softDelete(Long id) throws Exception;
    void deleteExpired() throws Exception;

    Optional<Token> findByTokenValue(String tokenValue) throws Exception;
    Optional<Token> findByUsername(String username) throws Exception;
}