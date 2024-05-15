package com.mabmab;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class EntityManagerFactorySingleton {
    private static EntityManagerFactory entityManagerFactory;

    private EntityManagerFactorySingleton() {
        // Private constructor to prevent instantiation
    }

    public static synchronized EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null) {
            entityManagerFactory = Persistence.createEntityManagerFactory("com.mabmab.jpa");
        }
        return entityManagerFactory;
    }

    public static synchronized void closeEntityManagerFactory() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
    }
}
