package app.daos;

import app.entities.Announcement;
import app.entities.Holiday;
import app.entities.User;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public class HolidayDAO implements DAOI<Holiday, Integer> {

    private final EntityManager em;

    public HolidayDAO(EntityManager em) {
        this.em = em;
    }

    @Override
    public Holiday create(Holiday holiday) {
        em.getTransaction().begin();
        em.persist(holiday);
        em.getTransaction().commit();
        return holiday;
    }

    @Override
    public List<Holiday> getAll() {
        List<Holiday> holidays =
                em.createQuery("SELECT c FROM Holiday c", Holiday.class)
                        .getResultList();
        return holidays;
    }

    @Override
    public Optional<Holiday> getById(Integer id) {
        Holiday holiday = em.find(Holiday.class, id);
        return Optional.ofNullable(holiday);
    }

    @Override
    public Holiday update(Holiday holiday) {
        em.getTransaction().begin();
        Holiday merged = em.merge(holiday);
        em.getTransaction().commit();
        return merged;
    }

    @Override
    public void delete(Integer id) {
        em.getTransaction().begin();
        Holiday holiday = em.find(Holiday.class, id);
        if (holiday != null) {
            em.remove(holiday);
        }
        em.getTransaction().commit();
    }
}
