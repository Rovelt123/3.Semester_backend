package app.services;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HolidayAPIServiceTest {

    HolidayAPIService service = new HolidayAPIService();
    private Map<LocalDate, String> holidays = service.getHolidays(2026);

    // ________________________________________________________

    @Test
    void getHoliday() {
        String name = service.getHoliday(LocalDate.of(2026, 12, 25), holidays);

        assertNotNull(name);
    }

    // ________________________________________________________

    @Test
    void isHoliday() {
        assertTrue(service.isHoliday(LocalDate.of(2026, 12, 25), holidays));
    }
}