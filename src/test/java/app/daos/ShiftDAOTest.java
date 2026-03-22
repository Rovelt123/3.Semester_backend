package app.daos;

import app.SetupTest;
import app.entities.Shift;
import app.entities.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ShiftDAOTest extends SetupTest {

    @Test
    void getShiftsByUserId() {
        User user = userDAO.create(testUser);

        Shift shift = new Shift();
        shift.setOwner(user);
        shift.setDate(LocalDate.now());
        shiftDAO.create(shift);

        List<Shift> shifts = shiftDAO.getShiftsByUserId(user.getId());

        assertEquals(1, shifts.size());
    }

    // ________________________________________________________

    @Test
    void getShiftsByDate() {
        User user = userDAO.create(testUser);

        LocalDate today = LocalDate.now();

        Shift shift = new Shift();
        shift.setOwner(user);
        shift.setDate(today);
        shiftDAO.create(shift);

        List<Shift> shifts = shiftDAO.getShiftsByDate(today);

        assertEquals(1, shifts.size());
    }
}