package app.daos;

import app.entities.Holiday;
import jakarta.persistence.EntityManager;


public class HolidayDAO extends EntityManagerDAO<Holiday> {

    public HolidayDAO(EntityManager em) {
        super(em, Holiday.class);
    }

}
