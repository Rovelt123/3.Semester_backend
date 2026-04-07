package app.daos;

import app.entities.Responsibility;
import jakarta.persistence.EntityManager;

public class ResponsibilityDAO  extends EntityManagerDAO<Responsibility> {

    public ResponsibilityDAO(EntityManager em) {
        super(em, Responsibility.class);
    }

    // ________________________________________________________

    public Responsibility getByName(String name) {

        String jpql = "SELECT r FROM Responsibility r WHERE r.name = :name";

        return executeQuery(() ->
            em.createQuery(jpql, Responsibility.class)
            .setParameter("name", name)
            .getSingleResult()

        );
    }

}

