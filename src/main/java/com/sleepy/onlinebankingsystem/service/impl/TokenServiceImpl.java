package com.sleepy.onlinebankingsystem.service.impl;

import com.sleepy.onlinebankingsystem.model.entity.Token;
import com.sleepy.onlinebankingsystem.repository.TokenRepository;
import com.sleepy.onlinebankingsystem.service.TokenService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@ApplicationScoped
public class TokenServiceImpl implements TokenService {

    @Inject
    TokenRepository tokenRepository;

    @Transactional
    @Override
    public Token save(Token token) throws Exception {
        return tokenRepository.save(token);
    }

    @Transactional
    @Override
    public void softDelete(Long id) throws Exception {
        tokenRepository.softDelete(id);
    }

    @Transactional
    @Override
    public void deleteExpired() throws Exception {
        tokenRepository.findAllIncludingDeleted().stream()
                .filter(Token::isExpired)
                .forEach(t -> tokenRepository.hardDelete(t.getId()));
    }

    @Override
    public Optional<Token> findByTokenValue(String tokenValue) throws Exception {
        return tokenRepository.findByTokenValue(tokenValue);
    }

    @Override
    public Optional<Token> findByUsername(String username) throws Exception {
        return tokenRepository.findByUsername(username);
    }
}