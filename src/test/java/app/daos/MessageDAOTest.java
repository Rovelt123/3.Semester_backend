package app.daos;

import app.configs.TestHibernateConfig;
import app.entities.Message;
import app.entities.User;
import app.enums.Role;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MessageDAOTest {

    private EntityManagerFactory emf;
    private EntityManager em;
    private MessageDAO messageDAO;
    private User testUser;

    @BeforeAll
    void setupAll() {
        emf = TestHibernateConfig.getTestEmf();
    }

    @BeforeEach
    void setup() {
        em = emf.createEntityManager();
        messageDAO = new MessageDAO(em);
        testUser = new User("Message Sender", Role.USER);
        em.getTransaction().begin();
        em.persist(testUser);
        em.getTransaction().commit();
    }

    @AfterEach
    void tearDown() {
        if (em.isOpen()) {
            em.close();
        }
    }

    @AfterAll
    void tearDownAll() {
        if (emf.isOpen()) {
            emf.close();
        }
    }

    @Test
    void create() {
        Message message = new Message(testUser, "Hello world");
        messageDAO.create(message);

        Optional<Message> found = messageDAO.getById(message.getId());
        assertTrue(found.isPresent());
        assertEquals("Hello world", found.get().getContent());
        assertEquals(testUser.getId(), found.get().getSender().getId());
    }

    @Test
    void getAll() {
        Message m1 = new Message(testUser, "Msg1");
        Message m2 = new Message(testUser, "Msg2");
        messageDAO.create(m1);
        messageDAO.create(m2);

        List<Message> messages = messageDAO.getAll();
        assertTrue(messages.size() >= 2);
    }

    @Test
    void update() {
        Message message = new Message(testUser, "Old Content");
        messageDAO.create(message);

        message.setContent("New Content");
        messageDAO.update(message);

        Message updated = messageDAO.getById(message.getId()).orElseThrow();
        assertEquals("New Content", updated.getContent());
    }

    @Test
    void delete() {
        Message message = new Message(testUser, "To delete");
        messageDAO.create(message);

        int id = message.getId();
        messageDAO.delete(id);
        assertTrue(messageDAO.getById(id).isEmpty());
    }
}
