package com.sleepy.onlinebankingsystem.service.impl;

import com.sleepy.onlinebankingsystem.model.entity.User;
import com.sleepy.onlinebankingsystem.repository.UserRepository;
import com.sleepy.onlinebankingsystem.service.UserService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
@ApplicationScoped
public class UserServiceImpl implements UserService {

    @Inject
    UserRepository userRepository;

    @Transactional
    @Override
    public User save(User user) throws Exception {
        log.info("Saving user: {}", user.getUsername());

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists: " + user.getUsername());
        }
        if (userRepository.findByNationalCode(user.getNationalCode()).isPresent()) {
            throw new IllegalArgumentException("National code already exists: " + user.getNationalCode());
        }

        user.setActive(false);
        return userRepository.save(user);
    }

    @Transactional
    @Override
    public User update(User user) throws Exception {
        if (user.getId() == null) throw new IllegalArgumentException("ID is required for update");
        return userRepository.save(user);
    }

    @Transactional
    @Override
    public void softDelete(Long id) throws Exception {
        userRepository.softDelete(id);
    }

    @Transactional
    @Override
    public void softDeleteByUsername(String username) throws Exception {
        userRepository.findByUsername(username).ifPresent(user -> userRepository.softDelete(user.getId()));
    }

    @Override
    public Optional<User> findById(Long id) throws Exception {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findByUsername(String username) throws Exception {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findByNationalCode(String nationalCode) throws Exception {
        return userRepository.findByNationalCode(nationalCode);
    }

    @Override
    public List<User> findAll(int page, int size) throws Exception {
        return userRepository.findAll(page, size);
    }

    @Override
    public List<User> findActiveUsers() throws Exception {
        return userRepository.findActiveUsers();
    }
}