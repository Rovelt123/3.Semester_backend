package app.daos;

import app.entities.Responsibility;
import app.entities.User;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public class UserDAO extends EntityManagerDAO<User>{

    public UserDAO(EntityManager em) {
        super(em, User.class);
    }

    public void addResponsibilityToUser(User user, Responsibility responsibility) {
        em.getTransaction().begin();
        if (!user.getResponsibilities().contains(responsibility)) {
            user.getResponsibilities().add(responsibility);
            em.merge(user);
        }
        em.getTransaction().commit();
    }

    public void removeResponsibilityFromUser(User user, Responsibility responsibility) {
        em.getTransaction().begin();
        user.getResponsibilities().remove(responsibility);
        em.merge(user);
        em.getTransaction().commit();
    }


}

