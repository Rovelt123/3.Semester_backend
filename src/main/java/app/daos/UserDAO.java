package app.daos;

import app.entities.Responsibility;
import app.entities.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

public class UserDAO extends EntityManagerDAO<User>{

    public UserDAO(EntityManager em) {
        super(em, User.class);
    }

    public User getByUsername(String username) {
        String jpql = "SELECT u FROM User u WHERE u.username = :username";

        try {
            return executeQuery(() ->
                    em.createQuery(jpql, User.class)
                            .setParameter("username", username)
                            .getSingleResult()
            );
        } catch (NoResultException e) {
            return null;
        }
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

