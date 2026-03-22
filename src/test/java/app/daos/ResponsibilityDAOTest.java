package app.daos;

import app.SetupTest;
import app.entities.Responsibility;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ResponsibilityDAOTest extends SetupTest {

    @Test
    void initializeResponsibilities() {
        responsibilityDAO.initializeResponsibilities();

        List<Responsibility> all = responsibilityDAO.getAll();

        assertFalse(all.isEmpty());
    }

    // ________________________________________________________

    @Test
    void getByName() {
        responsibilityDAO.initializeResponsibilities();

        Responsibility r = responsibilityDAO.getAll().get(0);

        Responsibility found = responsibilityDAO.getByName(r.getName());

        assertNotNull(found);
        assertEquals(r.getName(), found.getName());
    }
}
