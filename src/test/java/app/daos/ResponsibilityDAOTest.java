package app.daos;

import app.configs.TestHibernateConfig;
import app.entities.Responsibility;
import app.enums.Responsibilities;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ResponsibilityDAOTest {

    private EntityManagerFactory emf;
    private EntityManager em;
    private ResponsibilityDAO responsibilityDAO;

    @BeforeAll
    void setupAll() {
        emf = TestHibernateConfig.getTestEmf();
    }

    @BeforeEach
    void setup() {
        em = emf.createEntityManager();
        responsibilityDAO = new ResponsibilityDAO(em);
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
        Responsibility responsibility = new Responsibility(Responsibilities.PLANNER.getDisplayName());
        responsibilityDAO.create(responsibility);

        Responsibility found = responsibilityDAO.getById(responsibility.getId());
        assertNotNull(found);
        assertEquals(Responsibilities.PLANNER.getDisplayName(), found.getName());
    }

    @Test
    void getAll() {
        Responsibility r1 = new Responsibility(Responsibilities.CASHIER.getDisplayName());
        Responsibility r2 = new Responsibility(Responsibilities.DRIVER.getDisplayName());
        responsibilityDAO.create(r1);
        responsibilityDAO.create(r2);

        List<Responsibility> all = responsibilityDAO.getAll();
        assertNotNull(all);
        assertTrue(all.size() >= 2);
    }

    @Test
    void getById() {
        Responsibility responsibility = new Responsibility(Responsibilities.RECEPTUR.getDisplayName());
        responsibilityDAO.create(responsibility);

        Responsibility found = responsibilityDAO.getById(responsibility.getId());
        assertNotNull(found);
        assertEquals(Responsibilities.RECEPTUR.getDisplayName(), found.getName());
    }

    @Test
    void update() {
        Responsibility responsibility = new Responsibility(Responsibilities.PLANNER.getDisplayName());
        responsibilityDAO.create(responsibility);


        responsibility.setName(Responsibilities.CASHIER.getDisplayName());
        responsibilityDAO.update(responsibility);

        Responsibility updated = responsibilityDAO.getById(responsibility.getId());
        assertNotNull(updated);
        assertEquals(Responsibilities.CASHIER.getDisplayName(), updated.getName());
    }

    @Test
    void delete() {
        Responsibility responsibility = new Responsibility(Responsibilities.DRIVER.getDisplayName());
        responsibilityDAO.create(responsibility);

        Integer id = responsibility.getId();
        responsibilityDAO.deleteById(id);

        Responsibility deleted = responsibilityDAO.getById(id);
        assertNull(deleted);
    }

    @Test
    void initializeResponsibilities_shouldInsertAllEnumValues() {
        responsibilityDAO.initializeResponsibilities();

        List<Responsibility> all = responsibilityDAO.getAll();

        for (Responsibilities r : Responsibilities.values()) {
            boolean exists = all.stream()
                    .anyMatch(res -> res.getName().equals(r.getDisplayName()));
            assertTrue(exists);
        }
    }

    @Test
    void getByName_shouldReturnCorrectResponsibility() {
        responsibilityDAO.initializeResponsibilities();

        String name = Responsibilities.CASHIER.getDisplayName();
        Responsibility r = responsibilityDAO.getByName(name);

        assertNotNull(r);
        assertEquals(name, r.getName());
    }



}
