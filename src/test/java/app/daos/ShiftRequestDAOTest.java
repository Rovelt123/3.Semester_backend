package app.daos;

import app.Main;
import app.configs.TestHibernateConfig;
import app.entities.Shift;
import app.entities.ShiftRequest;
import app.entities.User;
import app.enums.Role;
import app.enums.ShiftStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ShiftRequestDAOTest {

    private EntityManagerFactory emf;
    private EntityManager em;
    private ShiftRequestDAO shiftRequestDAO;
    private UserDAO userDAO;
    private ShiftDAO shiftDAO;
    private User testUser;
    private Shift testShift;

    @BeforeAll
    void setupAll() {
        emf = TestHibernateConfig.getTestEmf();
    }

    @BeforeEach
    void setup() {
        em = emf.createEntityManager();
        shiftRequestDAO = new ShiftRequestDAO(em);
        userDAO = new UserDAO(em);
        shiftDAO = new ShiftDAO(em);

        testUser = new User("Test User", Role.USER, "user", "user");
        userDAO.create(testUser);

        testShift = new Shift("Shift", testUser, LocalDate.now(), LocalTime.now(), LocalTime.now().plusHours(8));
        shiftDAO.create(testShift);
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
        ShiftRequest shiftRequest = new ShiftRequest(testUser, testShift);
        shiftRequestDAO.create(shiftRequest);

        ShiftRequest found = shiftRequestDAO.getById(shiftRequest.getId());
        assertNotNull(found);
        assertEquals(testUser.getId(), Main.setup.getUserDAO().getById(found.getRequesterID()));
        assertEquals(testShift.getId(), found.getShift().getId());
    }

    @Test
    void getAll() {
        ShiftRequest sr1 = new ShiftRequest(testUser, testShift);
        ShiftRequest sr2 = new ShiftRequest(testUser, testShift);
        shiftRequestDAO.create(sr1);
        shiftRequestDAO.create(sr2);

        List<ShiftRequest> allRequests = shiftRequestDAO.getAll();
        assertNotNull(allRequests);
        assertTrue(allRequests.size() >= 2);
    }

    @Test
    void getById() {
        ShiftRequest shiftRequest = new ShiftRequest(testUser, testShift);
        shiftRequestDAO.create(shiftRequest);

        ShiftRequest found = shiftRequestDAO.getById(shiftRequest.getId());
        assertNotNull(found);
        assertEquals(shiftRequest.getId(), found.getId());
    }


    @Test
    void update() {
        ShiftRequest shiftRequest = new ShiftRequest(testUser, testShift);
        shiftRequestDAO.create(shiftRequest);

        shiftRequest.setStatus(ShiftStatus.APPROVED);
        shiftRequestDAO.update(shiftRequest);

        ShiftRequest updated = shiftRequestDAO.getById(shiftRequest.getId());
        assertNotNull(updated);
        assertEquals("APPROVED", updated.getStatus().getDisplayName());
    }

    @Test
    void delete() {
        ShiftRequest shiftRequest = new ShiftRequest(testUser, testShift);
        shiftRequestDAO.create(shiftRequest);

        Integer id = shiftRequest.getId();
        shiftRequestDAO.deleteById(id);

        assertNull(shiftRequestDAO.getById(id));
    }
}