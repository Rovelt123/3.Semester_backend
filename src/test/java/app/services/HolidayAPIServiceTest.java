package app.services;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class HolidayAPIServiceTest {

    @Test
    void loadHolidays() {
        HolidayAPIService service = new HolidayAPIService();
        service.loadHolidays(2026);

        assertTrue(service.isHoliday(LocalDate.of(2026, 12, 25)));
    }

    @Test
    void getHoliday() {
        HolidayAPIService service = new HolidayAPIService();
        service.loadHolidays(2026);

        String name = service.getHoliday(LocalDate.of(2026, 12, 25));

        assertNotNull(name);
    }

    @Test
    void isHoliday() {
        HolidayAPIService service = new HolidayAPIService();
        service.loadHolidays(2026);

        assertTrue(service.isHoliday(LocalDate.of(2026, 12, 25)));
    }
}