package app.daos;

import app.entities.Responsibility;
import app.entities.User;
import app.enums.Role;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.List;

public class UserDAO extends EntityManagerDAO<User>{

    public UserDAO(EntityManager em) {
        super(em, User.class);
    }

    // ________________________________________________________

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

    // ________________________________________________________

    public List<User> getUsersByRole(Role role) {
        String jpql = "SELECT u FROM User u JOIN u.roles r WHERE r = :role";
        return executeQuery(() ->
            em.createQuery(jpql, User.class)
            .setParameter("role", role)
            .getResultList()
        );
    }

    // ________________________________________________________

    public List<User> getUsersByResponsibility(String name) {
        String jpql = "SELECT u FROM User u JOIN u.responsibilities r WHERE LOWER(r.name) = LOWER(:name)";
        return executeQuery(() ->
            em.createQuery(jpql, User.class)
            .setParameter("name", name)
            .getResultList()
        );
    }

}

