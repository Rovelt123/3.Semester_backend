package app.daos;

import app.entities.ShiftRequest;
import jakarta.persistence.EntityManager;

public class ShiftRequestDAO  extends EntityManagerDAO<ShiftRequest> {

    public ShiftRequestDAO(EntityManager em) {
        super(em, ShiftRequest.class);
    }

}
