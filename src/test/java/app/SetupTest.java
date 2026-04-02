package app;

import app.configs.TestHibernateConfig;
import app.daos.*;
import app.entities.User;
import app.enums.Role;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.Builder;
import org.junit.jupiter.api.*;

import java.util.Set;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class SetupTest {

    protected static EntityManagerFactory emf;
    protected EntityManager em;

    // ________________________________________________________

    protected UserDAO userDAO;
    protected ResponsibilityDAO responsibilityDAO;
    protected ShiftDAO shiftDAO;
    protected ResponseDAO responseDAO;
    protected ShiftRequestDAO shiftRequestDAO;

    // ________________________________________________________

    protected User testUser;
    protected User testUser2;

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
        responsibilityDAO = new ResponsibilityDAO(em);
        shiftDAO = new ShiftDAO(em);
        responseDAO = new ResponseDAO(em);
        shiftRequestDAO = new ShiftRequestDAO(em);


        testUser = User.builder()
                .firstname("John")
                .lastname("Doe")
                .roles(Set.of(Role.USER))
                .username("john123")
                .password("123")
                .build();

        testUser2 = User.builder()
                .firstname("Gert")
                .lastname("Hansen")
                .roles(Set.of(Role.USER))
                .username("Testuser2")
                .password("123")
                .build();
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
