package app.daos;

import app.entities.Response;
import jakarta.persistence.EntityManager;

public class ResponseDAO extends EntityManagerDAO<Response>{

    public ResponseDAO(EntityManager em) {
        super(em, Response.class);
    }

}
