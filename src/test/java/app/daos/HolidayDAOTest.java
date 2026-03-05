package app.daos;

import app.configs.TestHibernateConfig;
import app.entities.Holiday;
import app.entities.User;
import app.enums.HolidayStatus;
import app.enums.Role;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HolidayDAOTest {

    private EntityManagerFactory emf;
    private EntityManager em;
    private HolidayDAO holidayDAO;
    private User testUser;

    @BeforeAll
    void setupAll() {
        emf = TestHibernateConfig.getTestEmf();
    }

    @BeforeEach
    void setup() {
        em = emf.createEntityManager();
        holidayDAO = new HolidayDAO(em);

        testUser = new User("Holiday User", Role.USER);
        em.getTransaction().begin();
        em.persist(testUser);
        em.getTransaction().commit();
    }

    @AfterEach
    void tearDown() {
        if (em.isOpen()) em.close();
    }

    @AfterAll
    void tearDownAll() {
        if (emf.isOpen()) emf.close();
    }

    @Test
    void create() {
        Holiday holiday = new Holiday(testUser, LocalDate.now(), LocalDate.now().plusDays(5));
        holidayDAO.create(holiday);

        Holiday found = holidayDAO.getById(holiday.getId());
        assertEquals(testUser.getId(), found.getUser().getId());
        assertEquals(HolidayStatus.PENDING, found.getStatus());
    }

    @Test
    void getAll() {
        Holiday h1 = new Holiday(testUser, LocalDate.now(), LocalDate.now().plusDays(2));
        Holiday h2 = new Holiday(testUser, LocalDate.now(), LocalDate.now().plusDays(3));
        holidayDAO.create(h1);
        holidayDAO.create(h2);

        List<Holiday> all = holidayDAO.getAll();
        assertTrue(all.size() >= 2);
    }

    @Test
    void update() {
        Holiday holiday = new Holiday(testUser, LocalDate.now(), LocalDate.now().plusDays(1));
        holidayDAO.create(holiday);

        holiday.approve();
        holidayDAO.update(holiday);

        Holiday updated = holidayDAO.getById(holiday.getId());
        assertEquals(HolidayStatus.APPROVED, updated.getStatus());
    }

    @Test
    void delete() {
        Holiday holiday = new Holiday(testUser, LocalDate.now(), LocalDate.now().plusDays(1));
        holidayDAO.create(holiday);

        int id = holiday.getId();
        holidayDAO.deleteById(id);
        assertNull(holidayDAO.getById(id));
    }
}
