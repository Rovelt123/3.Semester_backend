package app;

import app.configs.TestHibernateConfig;
import app.daos.*;
import app.entities.User;
import app.enums.Role;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.util.Set;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class SetupTest {

    protected static EntityManagerFactory emf;
    protected EntityManager em;

    // ________________________________________________________

    protected UserDAO userDAO;
    protected MessageDAO messageDAO;
    protected ResponsibilityDAO responsibilityDAO;
    protected ShiftDAO shiftDAO;
    protected ResponseDAO responseDAO;
    protected ShiftRequestDAO shiftRequestDAO;

    // ________________________________________________________

    protected User testUser;

    // ________________________________________________________

    @BeforeAll
    static void setupAll() { emf = TestHibernateConfig.getTestEmf(); }

    // ________________________________________________________

    @AfterAll
    static void closeAll() {
        emf.close();
    }

    // ________________________________________________________

    @BeforeEach
    void setup() {
        em = emf.createEntityManager();
        em.getTransaction().begin();

        userDAO = new UserDAO(em);
        messageDAO = new MessageDAO(em);
        responsibilityDAO = new ResponsibilityDAO(em);
        shiftDAO = new ShiftDAO(em);
        responseDAO = new ResponseDAO(em);
        shiftRequestDAO = new ShiftRequestDAO(em);

        testUser = new User(
            "John",
            "Doe",
            Set.of(Role.USER),
            "john123",
            "1234"
        );
    }

    // ________________________________________________________

    @AfterEach
    void cleanUp() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
        em.close();
    }

}
