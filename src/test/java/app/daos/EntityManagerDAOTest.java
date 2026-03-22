package app.daos;

import app.SetupTest;
import app.configs.TestHibernateConfig;
import app.entities.User;
import app.enums.Role;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class EntityManagerDAOTest extends SetupTest {

    @Test
    void create() {
        User created = userDAO.create(testUser);
        assertNotNull(created);
        assertNotEquals(0, created.getId());
    }

    // ________________________________________________________

    @Test
    void update() {
        userDAO.create(testUser);

        testUser.setFirstname("Updated");

        User updated = userDAO.update(testUser);

        assertEquals("Updated", updated.getFirstname());
    }

    // ________________________________________________________

    @Test
    void delete() {
        userDAO.create(testUser);

        User deleted = userDAO.delete(testUser);

        assertNotNull(deleted);
        assertNull(userDAO.getById(deleted.getId()));
    }

    // ________________________________________________________

    @Test
    void deleteById() {
        userDAO.create(testUser);

        User deleted = userDAO.deleteById(testUser.getId());

        assertNotNull(deleted);
        assertNull(userDAO.getById(testUser.getId()));
    }

    // ________________________________________________________

    @Test
    void getById() {
        userDAO.create(testUser);

        User found = userDAO.getById(testUser.getId());

        assertNotNull(found);
        assertEquals("john123", found.getUsername());
    }

    // ________________________________________________________

    @Test
    void getColumnById() {
        userDAO.create(testUser);

        String username = userDAO.getColumnById(testUser.getId(), "username");

        assertEquals("john123", username);
    }

    // ________________________________________________________

    @Test
    void getByColumn() {
        userDAO.create(testUser);

        List<User> users = userDAO.getByColumn("john123", "username");

        assertEquals(1, users.size());
    }

    // ________________________________________________________

    @Test
    void updateColumnById() {
        userDAO.create(testUser);

        int updatedRows = userDAO.updateColumnById(testUser.getId(), "firstname", "NewName");

        assertEquals(1, updatedRows);

        em.clear();

        User updated = userDAO.getById(testUser.getId());
        assertEquals("NewName", updated.getFirstname());
    }

    // ________________________________________________________

    @Test
    void existByColumn() {
        userDAO.create(testUser);

        boolean exists = userDAO.existByColumn("john123", "username");

        assertTrue(exists);
    }

    // ________________________________________________________

    @Test
    void findEntityByColumn() {
        userDAO.create(testUser);

        User found = userDAO.findEntityByColumn("john123", "username");

        assertNotNull(found);
        assertEquals("john123", found.getUsername());
    }

    // ________________________________________________________

    @Test
    void getAll() {
        userDAO.create(testUser);

        List<User> users = userDAO.getAll();

        assertFalse(users.isEmpty());
    }

    // ________________________________________________________

    @Test
    void deleteAll() {
        userDAO.create(testUser);

        userDAO.deleteAll();

        List<User> users = userDAO.getAll();

        assertTrue(users.isEmpty());
    }
}