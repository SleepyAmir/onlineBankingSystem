package com.sleepy.onlinebankingsystem.service;

import com.sleepy.onlinebankingsystem.model.entity.Role;
import com.sleepy.onlinebankingsystem.model.entity.User;

import java.util.List;
import java.util.Optional;

public interface RoleService {
    Role save(Role role) throws Exception;
    Role update(Role role) throws Exception;
    void softDelete(Long id) throws Exception;

    Optional<Role> findById(Long id) throws Exception;
    List<Role> findByUser(User user) throws Exception;
    Optional<Role> findByUsernameAndRoleName(String username, String roleName) throws Exception;
    List<Role> findByRoleName(String roleName) throws Exception;
    List<Role> findAll(int page, int size) throws Exception;
}