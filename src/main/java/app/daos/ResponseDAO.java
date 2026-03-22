package app.daos;

import app.entities.Response;
import jakarta.persistence.EntityManager;

import java.util.List;

public class ResponseDAO extends EntityManagerDAO<Response>{

    public ResponseDAO(EntityManager em) {
        super(em, Response.class);
    }

    // ________________________________________________________

    public Response getByUserAndShiftRequestId(int userId, int requestId) {
        String jpql = "SELECT r FROM Response r WHERE r.user.id = :uid AND r.shiftRequest.id = :rid";

        return em.createQuery(jpql, Response.class)
                .setParameter("uid", userId)
                .setParameter("rid", requestId)
                .getSingleResult();
    }

    // ________________________________________________________

    public List<Response> getByShiftRequestId(int requestId){

        return em.createQuery(
                "SELECT r FROM Response r WHERE r.shiftRequest.id = :rid",
                Response.class
            )
            .setParameter("rid", requestId)
            .getResultList();
    }
}
