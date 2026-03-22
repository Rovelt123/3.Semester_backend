package app.daos;

import app.entities.Message;
import jakarta.persistence.EntityManager;

import java.util.List;


public class MessageDAO  extends EntityManagerDAO<Message>{

    public MessageDAO(EntityManager em) {
        super(em, Message.class);
    }

    // ________________________________________________________

    public List<Message> getConversation(int user1, int user2){

        return em.createQuery(
            """
            SELECT m FROM Message m
            WHERE
            (m.sender.id = :u1 AND m.receiver.id = :u2)
            OR
            (m.sender.id = :u2 AND m.receiver.id = :u1)
            ORDER BY m.sentAt ASC
            """,
            Message.class
        )
        .setParameter("u1", user1)
        .setParameter("u2", user2)
        .getResultList();
    }

    // ________________________________________________________

    public List<Message> getMessagesForUser(int userId){

        return em.createQuery(
            """
            SELECT m FROM Message m
            WHERE m.sender.id = :uid
            OR m.receiver.id = :uid
            ORDER BY m.sentAt DESC
            """,
            Message.class
        )
        .setParameter("uid", userId)
        .getResultList();
    }

}

