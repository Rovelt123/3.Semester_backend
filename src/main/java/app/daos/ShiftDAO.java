package app.daos;

import app.entities.Shift;
import app.entities.User;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.util.List;

public class ShiftDAO  extends EntityManagerDAO<Shift> {

    public ShiftDAO(EntityManager em) {
        super(em, Shift.class);
    }

    // ________________________________________________________

    public List<Shift> getShiftsByUserId(int userId) {
        String jpql = "SELECT s FROM Shift s WHERE s.owner.id = :userId";

        return executeQuery(() ->
            em.createQuery(jpql, Shift.class)
            .setParameter("userId", userId)
            .getResultList()
        );
    }

    // ________________________________________________________

    public List<Shift> getShiftsByDate(LocalDate date) {
        String jpql = "SELECT s FROM Shift s WHERE s.date = :date";

        return executeQuery(() ->
            em.createQuery(jpql, Shift.class)
            .setParameter("date",date)
            .getResultList()
        );
    }

    // ________________________________________________________

    public Shift findByUserAndDate(int userID, LocalDate localDate){
        String jpql = "SELECT s FROM Shift s WHERE s.date = :date AND s.owner.id = :ownerID";

        return executeQuery(() ->
            em.createQuery(jpql, Shift.class)
            .setParameter("date", localDate)
            .setParameter("ownerID", userID)
            .getResultStream()
            .findFirst()
            .orElse(null)
        );
    }
}
