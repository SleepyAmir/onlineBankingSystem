package com.sleepy.onlinebankingsystem.service;

import com.sleepy.onlinebankingsystem.model.entity.Authority;
import com.sleepy.onlinebankingsystem.model.entity.Role;

import java.util.List;

public interface AuthorityService {
    Authority save(Authority authority) throws Exception;
    Authority update(Authority authority) throws Exception;
    void softDelete(Long id) throws Exception;

    List<Authority> findByRole(Role role) throws Exception;
    List<Authority> findByResource(String resource) throws Exception;
    List<Authority> findByResourceAndAction(String resource, String action) throws Exception;
    List<Authority> findAll(int page, int size) throws Exception;
}