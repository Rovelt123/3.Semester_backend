package app.daos;

import app.entities.Response;
import jakarta.persistence.EntityManager;

import java.util.List;

public class ResponseDAO extends EntityManagerDAO<Response>{

    public ResponseDAO(EntityManager em) {
        super(em, Response.class);
    }


    public List<Response> getByUserId(int userId){
        return em.createQuery(
            "SELECT r FROM Response r WHERE r.user.id = :uid",
            Response.class
        )
        .setParameter("uid", userId)
        .getResultList();
    }

    public List<Response> getByShiftRequestId(int requestId){

    return em.createQuery(
            "SELECT r FROM Response r WHERE r.shiftRequest.id = :rid",
            Response.class
        )
        .setParameter("rid", requestId)
        .getResultList();
    }
}
