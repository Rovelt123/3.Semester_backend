package app.daos;

import app.SetupTest;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/*class MessageDAOTest extends SetupTest {

    @Test
    void getConversation() {

        Message m1 = new Message();
        m1.setSender(user1);
        m1.setReceiver(user2);
        messageDAO.create(m1);

        Message m2 = new Message();
        m2.setSender(user2);
        m2.setReceiver(user1);
        messageDAO.create(m2);

        List<Message> conversation = messageDAO.getConversation(user1.getId(), user2.getId());

        assertEquals(2, conversation.size());
    }

    // ________________________________________________________

    @Test
    void getMessagesForUser() {

        Message m1 = new Message();
        m1.setSender(user1);
        m1.setReceiver(user2);
        messageDAO.create(m1);

        Message m2 = new Message();
        m2.setSender(user2);
        m2.setReceiver(user1);
        messageDAO.create(m2);

        List<Message> messages = messageDAO.getMessagesForUser(user1.getId());

        assertEquals(2, messages.size());
    }
}*/
