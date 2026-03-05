package app.daos;

import app.entities.Message;
import jakarta.persistence.EntityManager;


public class MessageDAO  extends EntityManagerDAO<Message>{

    public MessageDAO(EntityManager em) {
        super(em, Message.class);
    }


}

