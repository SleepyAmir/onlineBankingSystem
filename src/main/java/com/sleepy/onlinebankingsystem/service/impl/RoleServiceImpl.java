package com.sleepy.onlinebankingsystem.service.impl;

import com.sleepy.onlinebankingsystem.model.entity.Role;
import com.sleepy.onlinebankingsystem.model.entity.User;
import com.sleepy.onlinebankingsystem.repository.RoleRepository;
import com.sleepy.onlinebankingsystem.service.RoleService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
@ApplicationScoped
public class RoleServiceImpl implements RoleService {

    @Inject
    RoleRepository roleRepository;

    @Transactional
    @Override
    public Role save(Role role) throws Exception {
        return roleRepository.save(role);
    }

    @Transactional
    @Override
    public Role update(Role role) throws Exception {
        if (role.getId() == null) throw new IllegalArgumentException("ID is required");
        return roleRepository.save(role);
    }

    @Transactional
    @Override
    public void softDelete(Long id) throws Exception {
        roleRepository.softDelete(id);
    }

    @Override
    public Optional<Role> findById(Long id) throws Exception {
        return roleRepository.findById(id);
    }

    @Override
    public List<Role> findByUser(User user) throws Exception {
        return roleRepository.findByUser(user);
    }

    @Override
    public Optional<Role> findByUsernameAndRoleName(String username, String roleName) throws Exception {
        return roleRepository.findByUsernameAndRoleName(username, roleName);
    }

    @Override
    public List<Role> findByRoleName(String roleName) throws Exception {
        return roleRepository.findByRoleName(roleName);
    }

    @Override
    public List<Role> findAll(int page, int size) throws Exception {
        return roleRepository.findAll(page, size);
    }
}