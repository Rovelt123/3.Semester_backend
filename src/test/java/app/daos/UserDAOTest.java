package app.daos;

import app.configs.TestHibernateConfig;
import app.entities.Responsibility;
import app.entities.User;
import app.enums.Responsibilities;
import app.enums.Role;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserDAOTest {

    private EntityManagerFactory emf;
    private EntityManager em;
    private UserDAO userDAO;
    private ResponsibilityDAO responsibilityDAO;

    @BeforeAll
    void setupAll() {
        emf = TestHibernateConfig.getTestEmf();
    }

    @BeforeEach
    void setup() {
        em = emf.createEntityManager();
        userDAO = new UserDAO(em);
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
        User user = new User("Test Student", Role.USER, "user", "user");
        userDAO.create(user);

        assertNotNull(userDAO.getById(user.getId()));
    }

    @Test
    void getAll() {
        User user = new User("Test Student", Role.USER, "user", "user");
        User user2 = new User("Test Student2", Role.CHEF, "user", "user");
        userDAO.create(user);
        userDAO.create(user2);
        List<User> userList = userDAO.getAll();
        assertNotNull(userList);
    }

    @Test
    void getById() {
        User user = new User("Find Me", Role.USER, "user", "user");
        userDAO.create(user);

        User found = userDAO.getById(user.getId());

        assertNotNull(found);
        assertEquals("Find Me", found.getFirstname());
    }


    @Test
    void update() {
        User user = new User("Old Name", Role.USER, "user", "user");
        userDAO.create(user);

        user.setFirstname("New Name");
        user.setRole(Role.CHEF);
        userDAO.update(user);

        User updated = userDAO.getById(user.getId());

        assertEquals("New Name", updated.getFirstname());
        assertEquals("Chefen", updated.getRole().getDisplayName());
    }

    @Test
    void delete() {
        User user = new User("To Delete", Role.USER, "user", "user");
        userDAO.create(user);

        Integer id = user.getId();
        userDAO.deleteById(id);

        assertNull(userDAO.getById(id));
    }

    @Test
    void addResponsibility() {
        responsibilityDAO.initializeResponsibilities();

        Responsibility planner = responsibilityDAO.getByName(Responsibilities.PLANNER.getDisplayName());

        User user = new User("Responsible User", Role.USER, "user", "user");
        userDAO.create(user);

        userDAO.addResponsibilityToUser(user, planner);

        User updatedUser = userDAO.getById(user.getId());
        assertEquals(1, updatedUser.getResponsibilities().size());
        assertEquals("Planning holidays", updatedUser.getResponsibilities().get(0).getName());
    }

    @Test
    void removeResponsibility() {
        responsibilityDAO.initializeResponsibilities();
        Responsibility planner = responsibilityDAO.getByName(Responsibilities.PLANNER.getDisplayName());
        Responsibility cashier = responsibilityDAO.getByName(Responsibilities.CASHIER.getDisplayName());

        User user = new User("Responsible User", Role.USER, "user", "user");
        userDAO.create(user);
        userDAO.addResponsibilityToUser(user, planner);
        userDAO.addResponsibilityToUser(user, cashier);

        User beforeRemoval = userDAO.getById(user.getId());
        assertEquals(2, beforeRemoval.getResponsibilities().size());

        userDAO.removeResponsibilityFromUser(user, planner);

        User afterRemoval = userDAO.getById(user.getId());
        assertEquals(1, afterRemoval.getResponsibilities().size());
    }


}