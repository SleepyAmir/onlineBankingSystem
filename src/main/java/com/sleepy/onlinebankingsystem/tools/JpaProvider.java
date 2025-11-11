package com.sleepy.onlinebankingsystem.tools;


import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import lombok.Getter;


public class JpaProvider {
    @Getter
    private static JpaProvider provider = new JpaProvider();
    private static EntityManagerFactory factory = Persistence.createEntityManagerFactory("organization");

    private JpaProvider() {
    }

    public EntityManager getEntityManager() {
        return factory.createEntityManager();
    }
}
