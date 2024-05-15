package com.mabmab;

import java.util.function.Consumer;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import junit.framework.TestCase;

import static java.lang.System.out;
import static java.time.LocalDateTime.now;

import static jakarta.persistence.Persistence.createEntityManagerFactory;

public class JPAIllustrationTest extends TestCase {
    private EntityManagerFactory entityManagerFactory;

    @Override
    protected void setUp() {
        // an EntityManagerFactory is set up once for an application
        // IMPORTANT: notice how the name here matches the name we
        // gave the persistence-unit in persistence.xml
        entityManagerFactory = createEntityManagerFactory("com.mabmab.jpa");
    }

    @Override
    protected void tearDown() {
        entityManagerFactory.close();
    }

    public void testBasicUsage() {
        // create a couple of events...
        inTransaction(entityManager -> {
            entityManager.persist(new Candidato("Robs", "robs@gmail.com", "19052000"));
            entityManager.persist(new Candidato("Jimmy", "macaco@simio.com", "banana"));
        });

        // now lets pull events from the database and list them
        inTransaction(entityManager -> {
            entityManager.createQuery("select e from Candidato e", Candidato.class).getResultList()
                    .forEach(event -> out.println("Candidato (" + event.getNome() + ") : " + event.getEmail() + " " + event.getSenha()));
        });
    }

    void inTransaction(Consumer<EntityManager> work) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            work.accept(entityManager);
            transaction.commit();
        }
        catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
        finally {
            entityManager.close();
        }
    }

}
