package app.daos;

import app.entities.Shift;
import app.entities.ShiftRequest;
import app.entities.User;
import app.enums.ShiftStatus;
import jakarta.persistence.EntityManager;

import java.util.List;

public class ShiftRequestDAO  extends EntityManagerDAO<ShiftRequest> {

    public ShiftRequestDAO(EntityManager em) {
        super(em, ShiftRequest.class);
    }

    public Shift takeShift(int requestId, int userId){

            ShiftRequest request = em.find(ShiftRequest.class, requestId);

            if(request == null){
                throw new RuntimeException("ShiftRequest not found");
            }

            if(request.getStatus() != ShiftStatus.WAITING){
                throw new RuntimeException("Shift already taken");
            }

            User newOwner = em.find(User.class, userId);
            Shift shift = request.getShift();

            // check overlap
            List<Shift> overlapping = em.createQuery(
                            """
                            SELECT s FROM Shift s
                            WHERE s.owner.id = :uid
                            AND s.date = :date
                            AND (
                                    (s.startTime <= :end AND s.endTime >= :start)
                            )
                            """,
                            Shift.class
                    )
                    .setParameter("uid", userId)
                    .setParameter("date", shift.getDate())
                    .setParameter("start", shift.getStartTime())
                    .setParameter("end", shift.getEndTime())
                    .getResultList();

            if(!overlapping.isEmpty()){
                throw new RuntimeException("User already has shift in this time period");
            }

            // transfer shift
            shift.setOwner(newOwner);

            // mark request solved
            request.solve();

            em.merge(shift);
            em.merge(request);

            return shift;
    }

}
