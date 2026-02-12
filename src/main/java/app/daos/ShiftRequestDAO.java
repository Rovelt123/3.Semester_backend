package app.daos;

import app.entities.ShiftRequest;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public class ShiftRequestDAO implements DAOI<ShiftRequest, Integer> {

    private final EntityManager em;

    public ShiftRequestDAO(EntityManager em) {
        this.em = em;
    }

    @Override
    public ShiftRequest create(ShiftRequest shiftRequest) {
        em.getTransaction().begin();
        em.persist(shiftRequest);
        em.getTransaction().commit();
        return shiftRequest;
    }

    @Override
    public List<ShiftRequest> getAll() {
        List<ShiftRequest> shiftRequests =
                em.createQuery("SELECT c FROM ShiftRequest c", ShiftRequest.class)
                        .getResultList();
        return shiftRequests;
    }

    @Override
    public Optional<ShiftRequest> getById(Integer id) {
        ShiftRequest shiftRequest = em.find(ShiftRequest.class, id);
        return Optional.ofNullable(shiftRequest);
    }

    @Override
    public ShiftRequest update(ShiftRequest shiftRequest) {
        em.getTransaction().begin();
        ShiftRequest merged = em.merge(shiftRequest);
        em.getTransaction().commit();
        return merged;
    }

    @Override
    public void delete(Integer id) {
        em.getTransaction().begin();
        ShiftRequest shiftRequest = em.find(ShiftRequest.class, id);
        if (shiftRequest != null) {
            em.remove(shiftRequest);
        }
        em.getTransaction().commit();
    }
}
