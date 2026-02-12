package app.daos;

import app.entities.Announcement;
import app.entities.Message;
import app.entities.User;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public class MessageDAO implements DAOI<Message, Integer>{

    private final EntityManager em;

    public MessageDAO(EntityManager em) {
        this.em = em;
    }

    @Override
    public Message create(Message message) {
        em.getTransaction().begin();
        em.persist(message);
        em.getTransaction().commit();
        return message;
    }

    @Override
    public List<Message> getAll() {
        List<Message> messages =
                em.createQuery("SELECT c FROM Message c", Message.class)
                        .getResultList();
        return messages;
    }

    @Override
    public Optional<Message> getById(Integer id) {
        Message message = em.find(Message.class, id);
        return Optional.ofNullable(message);
    }

    @Override
    public Message update(Message message) {
        em.getTransaction().begin();
        Message merged = em.merge(message);
        em.getTransaction().commit();
        return merged;
    }

    @Override
    public void delete(Integer id) {
        em.getTransaction().begin();
        Message message = em.find(Message.class, id);
        if (message != null) {
            em.remove(message);
        }
        em.getTransaction().commit();
    }
}

