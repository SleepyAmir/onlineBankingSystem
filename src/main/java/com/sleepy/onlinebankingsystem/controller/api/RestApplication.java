package com.sleepy.onlinebankingsystem.controller.api;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import lombok.extern.slf4j.Slf4j;

/**
 * JAX-RS Application Configuration
 * Base path for all REST API endpoints: /api
 */
@Slf4j
@ApplicationPath("/api")
public class RestApplication extends Application {

    public RestApplication() {
        log.info("REST Application initialized - Base path: /api");
    }

    // JAX-RS will automatically discover and register all @Path annotated classes
    // No need to override getClasses() or getSingletons() for automatic discovery
}