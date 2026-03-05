package app.daos;

import app.entities.Shift;
import jakarta.persistence.EntityManager;

public class ShiftDAO  extends EntityManagerDAO<Shift> {

    public ShiftDAO(EntityManager em) {
        super(em, Shift.class);
    }


}
