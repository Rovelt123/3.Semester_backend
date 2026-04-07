package app.daos;

import app.SetupTest;
import app.entities.Responsibility;
import app.entities.User;
import app.enums.Role;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserDAOTest extends SetupTest {

    @Test
    void getByUsername() {
        userDAO.create(testUser);

        User found = userDAO.getByUsername("john123");

        assertNotNull(found);
    }

    // ________________________________________________________

    @Test
    void getUsersByRole() {
        userDAO.create(testUser);

        List<User> users = userDAO.getUsersByRole(Role.USER);

        assertFalse(users.isEmpty());
    }

    // ________________________________________________________

    @Test
    void getUsersByResponsibility() {
        User user = userDAO.create(testUser);

        Responsibility r = responsibilityDAO.create(Responsibility.builder().name("Cleaning").build());


        user.getResponsibilities().add(r);

        userDAO.update(user);

        List<User> users = userDAO.getUsersByResponsibility("Cleaning");

        assertEquals(1, users.size());
    }
}