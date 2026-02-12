package app.daos;

import app.entities.Responsibility;
import app.enums.Responsibilities;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public class ResponsibilityDAO implements DAOI<Responsibility, Integer> {

    private final EntityManager em;

    public ResponsibilityDAO(EntityManager em) {
        this.em = em;
    }

    @Override
    public Responsibility create(Responsibility responsibility) {
        em.getTransaction().begin();
        em.persist(responsibility);
        em.getTransaction().commit();
        return responsibility;
    }

    @Override
    public List<Responsibility> getAll() {
        List<Responsibility> Responsibilities =
                em.createQuery("SELECT c FROM Responsibility c", Responsibility.class)
                        .getResultList();
        return Responsibilities;
    }

    @Override
    public Optional<Responsibility> getById(Integer id) {
        Responsibility responsibility = em.find(Responsibility.class, id);
        return Optional.ofNullable(responsibility);
    }

    @Override
    public Responsibility update(Responsibility responsibility) {
        em.getTransaction().begin();
        Responsibility merged = em.merge(responsibility);
        em.getTransaction().commit();
        return merged;
    }

    @Override
    public void delete(Integer id) {
        em.getTransaction().begin();
        Responsibility responsibility = em.find(Responsibility.class, id);
        if (responsibility != null) {
            em.remove(responsibility);
        }
        em.getTransaction().commit();
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

