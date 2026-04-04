package app.daos;

import app.SetupTest;
import app.entities.Responsibility;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResponsibilityDAOTest extends SetupTest {


    @Test
    void getByName() {
        Responsibility res = Responsibility.builder().name("TEST").build();
        responsibilityDAO.create(res);

        Responsibility r = responsibilityDAO.getById(1);

        Responsibility found = responsibilityDAO.getByName(r.getName());

        assertNotNull(found);
        assertEquals(r.getName(), found.getName());
    }
}
