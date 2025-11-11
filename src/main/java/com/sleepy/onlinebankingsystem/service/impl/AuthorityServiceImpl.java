package com.sleepy.onlinebankingsystem.service.impl;

import com.sleepy.onlinebankingsystem.model.entity.Authority;
import com.sleepy.onlinebankingsystem.model.entity.Role;
import com.sleepy.onlinebankingsystem.repository.AuthorityRepository;
import com.sleepy.onlinebankingsystem.service.AuthorityService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@ApplicationScoped
public class AuthorityServiceImpl implements AuthorityService {

    @Inject
    AuthorityRepository authorityRepository;

    @Transactional
    @Override
    public Authority save(Authority authority) throws Exception {
        return authorityRepository.save(authority);
    }

    @Transactional
    @Override
    public Authority update(Authority authority) throws Exception {
        if (authority.getId() == null) throw new IllegalArgumentException("ID is required");
        return authorityRepository.save(authority);
    }

    @Transactional
    @Override
    public void softDelete(Long id) throws Exception {
        authorityRepository.softDelete(id);
    }

    @Override
    public List<Authority> findByRole(Role role) throws Exception {
        return authorityRepository.findByRole(role);
    }

    @Override
    public List<Authority> findByResource(String resource) throws Exception {
        return authorityRepository.findByResource(resource);
    }

    @Override
    public List<Authority> findByResourceAndAction(String resource, String action) throws Exception {
        return authorityRepository.findByResourceAndAction(resource, action);
    }

    @Override
    public List<Authority> findAll(int page, int size) throws Exception {
        return authorityRepository.findAll(page, size);
    }
}