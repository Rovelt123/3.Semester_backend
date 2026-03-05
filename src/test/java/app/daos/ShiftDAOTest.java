package app.daos;

import app.configs.TestHibernateConfig;
import app.entities.Shift;
import app.entities.ShiftRequest;
import app.entities.User;
import app.enums.Role;
import app.services.HolidayAPIService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ShiftDAOTest {

    private EntityManagerFactory emf;
    private EntityManager em;
    private UserDAO userDAO;
    private ShiftDAO shiftDAO;
    private User testUser;

    @BeforeAll
    void setupAll() {
        emf = TestHibernateConfig.getTestEmf();
    }

    @BeforeEach
    void setup() {
        em = emf.createEntityManager();
        userDAO = new UserDAO(em);
        shiftDAO = new ShiftDAO(em);

        testUser = new User("Test User", Role.USER);
        userDAO.create(testUser);
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
        Shift shift = new Shift("Shift", testUser, LocalDate.now(), LocalTime.now(), LocalTime.now().plusHours(8));
        shiftDAO.create(shift);

        Shift found = shiftDAO.getById(shift.getId());

        assertNotNull(found);
    }

    @Test
    void getAll() {
        Shift shift1 = new Shift("Shift1", testUser, LocalDate.now(), LocalTime.now(), LocalTime.now().plusHours(8));
        shiftDAO.create(shift1);
        Shift shift2 = new Shift("Shift2", testUser, LocalDate.now().plusDays(2), LocalTime.now(), LocalTime.now().plusHours(2));
        shiftDAO.create(shift2);

        List<Shift> shifts = shiftDAO.getAll();

        assertNotNull(shifts);
        assertTrue(shifts.size() >= 2);
    }

    @Test
    void getById() {
        Shift shift = new Shift("Shift", testUser, LocalDate.now(), LocalTime.now(), LocalTime.now().plusHours(8));
        shiftDAO.create(shift);

        Shift found = shiftDAO.getById(shift.getId());

        assertNotNull(found);
        assertEquals(shift.getId(), found.getId());
    }

    @Test
    void update() {
        Shift shift = new Shift("Shift", testUser, LocalDate.now(), LocalTime.now(), LocalTime.now().plusHours(8));
        shiftDAO.create(shift);

        LocalDate newDate = LocalDate.now().plusDays(10);
        shift.setDate(newDate);
        shiftDAO.update(shift);

        Shift found = shiftDAO.getById(shift.getId());

        assertNotNull(found);
        assertEquals(newDate, shift.getDate());
    }

    @Test
    void delete() {
        Shift shift = new Shift("Shift", testUser, LocalDate.now(), LocalTime.now(), LocalTime.now().plusHours(8));
        shiftDAO.create(shift);

        Integer id = shift.getId();
        shiftDAO.deleteById(id);

        assertNull(shiftDAO.getById(id));

    }

    @Test
    void createShiftOnHoliday() {
        HolidayAPIService holidayAPIService = new HolidayAPIService();
        holidayAPIService.loadHolidays(2026);
        LocalDate holiday = LocalDate.of(2026, 12, 25);

        Shift shift = new Shift("Work shift", testUser, holiday, LocalTime.of(8,0), LocalTime.of(16,0));

        if (holidayAPIService.isHoliday(holiday)) {
            String name = holidayAPIService.getHoliday(holiday);
            shift.setTitle("FRI: " + name);
        }
        shiftDAO.create(shift);

        Shift found = shiftDAO.getById(shift.getId());

        assertNotNull(found);
        assertEquals("FRI: Juledag / 1. juledag",found.getTitle());
    }
}