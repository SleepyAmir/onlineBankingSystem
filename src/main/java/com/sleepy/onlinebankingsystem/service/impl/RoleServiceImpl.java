package com.sleepy.onlinebankingsystem.service.impl;

import com.sleepy.onlinebankingsystem.model.entity.Role;
import com.sleepy.onlinebankingsystem.model.entity.User;
import com.sleepy.onlinebankingsystem.model.enums.UserRole;
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
        if (role.getRole() == null) {
            throw new IllegalArgumentException("Role enum must not be null");
        }
        log.info("Saving role [{}] for user [{}]", role.getRole().name(),
                role.getUser() != null ? role.getUser().getUsername() : "N/A");
        return roleRepository.save(role);
    }

    @Transactional
    @Override
    public Role update(Role role) throws Exception {
        if (role.getId() == null) {
            throw new IllegalArgumentException("ID is required for update");
        }
        if (role.getRole() == null) {
            throw new IllegalArgumentException("Role enum must not be null");
        }
        return roleRepository.save(role);
    }

    @Transactional
    @Override
    public void softDelete(Long id) throws Exception {
        log.info("Soft-deleting role with ID: {}", id);
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
    public Optional<Role> findByUsernameAndRoleName(String username, UserRole roleName) throws Exception {
        return Optional.empty();
    }

    @Override
    public List<Role> findByRoleName(UserRole roleName) throws Exception {
        return List.of();
    }




    @Override
    public List<Role> findAll(int page, int size) throws Exception {
        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException("Invalid pagination: page >= 0, size > 0");
        }
        return roleRepository.findAll(page, size);
    }

    @Override
    public long countAll() throws Exception {
        return roleRepository.countAll();
    }
}