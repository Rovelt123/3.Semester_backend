package app.daos;

import app.entities.Responsibility;
import app.enums.Responsibilities;
import jakarta.persistence.EntityManager;

public class ResponsibilityDAO  extends EntityManagerDAO<Responsibility> {

    public ResponsibilityDAO(EntityManager em) {
        super(em, Responsibility.class);
    }

    public void initializeResponsibilities() {
        em.getTransaction().begin();

        for (Responsibilities r : Responsibilities.values()) {
            boolean exists = em.createQuery("SELECT 1 FROM Responsibility r WHERE r.name = :name")
                    .setParameter("name", r.getDisplayName())
                    .getResultStream()
                    .findFirst()
                    .isPresent();

            if (!exists) {
                em.persist(new Responsibility(r.getDisplayName()));
            }
        }

        em.getTransaction().commit();
    }


    public Responsibility getByName(String name) {
        return em.createQuery("SELECT r FROM Responsibility r WHERE r.name = :name", Responsibility.class)
                .setParameter("name", name)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

}

