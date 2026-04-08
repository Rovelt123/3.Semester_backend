package app.daos;

import app.SetupTest;
import app.entities.Shift;
import app.entities.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
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

    @Test
    void findByUserAndDate() {
        User user = userDAO.create(testUser);

        LocalDate today = LocalDate.now();

        Shift shift = new Shift();
        shift.setTitle("TESTS?!");
        shift.setOwner(user);
        shift.setDate(today);
        shiftDAO.create(shift);

        Shift found = shiftDAO.findByUserAndDate(user.getId(), today);

        assertNotNull(found);

    }

    @Test
    void shouldOnlyReturnShiftsForSpecificUser() {
        User user1 = userDAO.create(testUser);
        User user2 = userDAO.create(testUser2);

        Shift shift1 = Shift.builder()
            .owner(user1)
            .title("Shift for user1")
            .date(LocalDate.of(2025, 1, 1))
            .startTime(LocalTime.now())
            .endTime(LocalTime.now().plusHours(9))
            .build();

        Shift shift2 = Shift.builder()
                .owner(user2)
                .title("Shift for user2")
                .date(LocalDate.of(2025, 1, 1))
                .startTime(LocalTime.now())
                .endTime(LocalTime.now().plusHours(9))
                .build();

        shiftDAO.create(shift1);
        shiftDAO.create(shift2);

        List<Shift> result = shiftDAO.findByUserAndDateRange(
                user1.getId(),
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 2)
        );

        assertEquals(1, result.size());
        assertEquals(user1.getId(), result.get(0).getOwner().getId());
    }
}