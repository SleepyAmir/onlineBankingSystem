package com.sleepy.onlinebankingsystem.service;

import com.sleepy.onlinebankingsystem.model.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User save(User user) throws Exception;
    User update(User user) throws Exception;
    void softDelete(Long id) throws Exception;
    void softDeleteByUsername(String username) throws Exception;

    Optional<User> findById(Long id) throws Exception;
    Optional<User> findByUsername(String username) throws Exception;
    Optional<User> findByNationalCode(String nationalCode) throws Exception;
    List<User> findAll(int page, int size) throws Exception;
    List<User> findActiveUsers() throws Exception;
}