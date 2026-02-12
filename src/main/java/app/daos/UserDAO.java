package app.daos;

import app.entities.Responsibility;
import app.entities.User;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public class UserDAO implements DAOI<User, Integer>{

    private final EntityManager em;

    public UserDAO(EntityManager em) {
        this.em = em;
    }

    @Override
    public User create(User user) {
        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();
        return user;
    }

    @Override
    public List<User> getAll() {
        List<User> users =
                em.createQuery("SELECT c FROM User c", User.class)
                        .getResultList();
        return users;
    }

    @Override
    public Optional<User> getById(Integer id) {
        User user = em.find(User.class, id);
        return Optional.ofNullable(user);
    }

    @Override
    public User update(User user) {
        em.getTransaction().begin();
        User merged = em.merge(user);
        em.getTransaction().commit();
        return merged;
    }

    @Override
    public void delete(Integer id) {
        em.getTransaction().begin();
        User user = em.find(User.class, id);
        if (user != null) {
            em.remove(user);
        }
        em.getTransaction().commit();
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

