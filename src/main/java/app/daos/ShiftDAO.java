package app.daos;

import app.entities.Shift;
import app.entities.ShiftRequest;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public class ShiftDAO implements DAOI<Shift, Integer> {

    private final EntityManager em;

    public ShiftDAO(EntityManager em) {
        this.em = em;
    }

    @Override
    public Shift create(Shift shift) {
        em.getTransaction().begin();
        em.persist(shift);
        em.getTransaction().commit();
        return shift;
    }

    @Override
    public List<Shift> getAll() {
        List<Shift> shifts =
                em.createQuery("SELECT c FROM Shift c", Shift.class)
                        .getResultList();
        return shifts;
    }

    @Override
    public Optional<Shift> getById(Integer id) {
        Shift shifts = em.find(Shift.class, id);
        return Optional.ofNullable(shifts);
    }

    @Override
    public Shift update(Shift shift) {
        em.getTransaction().begin();
        Shift merged = em.merge(shift);
        em.getTransaction().commit();
        return merged;
    }

    @Override
    public void delete(Integer id) {
        em.getTransaction().begin();
        Shift shift = em.find(Shift.class, id);
        if (shift != null) {
            em.remove(shift);
        }
        em.getTransaction().commit();
    }
}
