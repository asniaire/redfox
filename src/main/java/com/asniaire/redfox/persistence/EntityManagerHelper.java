package com.asniaire.redfox.persistence;

import lombok.Getter;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public enum EntityManagerHelper {

    INSTANCE;

    private static final String PERSISTENCE_UNIT_NAME = "redfox-persistence-unit";

    @Getter private final EntityManagerFactory entityManagerFactory;

    EntityManagerHelper() {
        entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
    }

    public EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }

    public void destroy() {
        entityManagerFactory.close();
    }

}