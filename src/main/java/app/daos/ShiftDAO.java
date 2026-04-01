package app.daos;

import app.entities.Shift;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.util.List;

public class ShiftDAO  extends EntityManagerDAO<Shift> {

    public ShiftDAO(EntityManager em) {
        super(em, Shift.class);
    }

    // ________________________________________________________

    public List<Shift> getShiftsByUserId(int userId) {
        return em.createQuery(
        "SELECT s FROM Shift s WHERE s.owner.id = :userId", Shift.class)
            .setParameter("userId", userId).getResultList();
    }

    // ________________________________________________________

    public List<Shift> getShiftsByDate(LocalDate date) {
        return em.createQuery(
        "SELECT s FROM Shift s WHERE s.date = :date", Shift.class)
        .setParameter("date", date).getResultList();
    }
}
