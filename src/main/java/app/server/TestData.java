package app.server;

import app.Main;
import app.daos.*;
import app.entities.*;
import app.enums.Responsibilities;
import app.enums.Role;
import app.enums.ShiftStatus;
import app.services.HashService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

public class TestData {
    private static final ShiftDAO shiftDao = Main.setup.getShiftDAO();
    private static final UserDAO userDAO = Main.setup.getUserDAO();
    private static final ResponsibilityDAO responsibilityDAO = Main.setup.getRespDAO();
    private static final ShiftRequestDAO shiftRequestDAO = Main.setup.getShiftRequestDAO();
    private static final AnnouncementDAO announcementDAO = Main.setup.getAnnouncementDAO();
    private static final HolidayDAO holidayDAO = Main.setup.getHolidayDAO();
    private static final ResponseDAO responseDAO = Main.setup.getResponseDAO();

    // ________________________________________________________

    public static void generate() {

        if (userDAO.getById(1) != null) {
            return;
        }

        // ========================================================
        // USERS
        // ========================================================

        User admin = User.builder()
                .firstname("Admin User")
                .lastname("lastname")
                .roles(Set.of(Role.CHEF, Role.USER))
                .username("admin")
                .password(HashService.hashHelper("admin"))
                .build();

        User worker = User.builder()
                .firstname("Worker User")
                .lastname("lastname")
                .roles(Set.of(Role.USER))
                .username("user")
                .password(HashService.hashHelper("user"))
                .build();

        User worker2 = User.builder()
                .firstname("Worker User1")
                .lastname("lastname")
                .roles(Set.of(Role.USER))
                .username("user1")
                .password(HashService.hashHelper("user1"))
                .build();

        User worker3 = User.builder()
                .firstname("Worker User2")
                .lastname("lastname")
                .roles(Set.of(Role.USER))
                .username("user2")
                .password(HashService.hashHelper("user2"))
                .build();

        User worker4 = User.builder()
                .firstname("Worker User3")
                .lastname("lastname")
                .roles(Set.of(Role.USER))
                .username("user3")
                .password(HashService.hashHelper("user3"))
                .build();

        userDAO.create(admin);
        userDAO.create(worker);
        userDAO.create(worker2);
        userDAO.create(worker3);
        userDAO.create(worker4);

        // ========================================================
        // RESPONSIBILITIES
        // ========================================================

        for (Responsibilities r : Responsibilities.values()) {

            Responsibility entity = Responsibility.builder()
                    .name(r.getDisplayName())
                    .build();

            responsibilityDAO.create(entity);
        }



        worker.getResponsibilities().add(responsibilityDAO.getByName(Responsibilities.CASHIER.getDisplayName()));
        worker.getResponsibilities().add(responsibilityDAO.getByName(Responsibilities.DRIVER.getDisplayName()));
        userDAO.update(worker);
        admin.getResponsibilities().add(responsibilityDAO.getByName(Responsibilities.PLANNER.getDisplayName()));
        userDAO.update(admin);

        // ========================================================
        // SHIFTS
        // ========================================================

        Shift shift1 = Shift.builder()
            .title("Morning shift")
            .owner(worker)
            .date(LocalDate.now())
            .startTime(LocalTime.of(8,0))
            .endTime(LocalTime.of(16,0))
            .build();


        Shift shift2 = Shift.builder()
            .title("Evening Shift")
            .owner(worker2)
            .date(LocalDate.now().plusDays(1))
            .startTime(LocalTime.of(14,0))
            .endTime(LocalTime.of(22,0))
            .build();

        shiftDao.create(shift1);
        shiftDao.create(shift2);

        // ========================================================
        // SHIFT REQUESTS (swap / request)
        // ========================================================

        ShiftRequest request = ShiftRequest.builder()
            .requester(worker)
            .shift(shift1)
            .build();

        shiftRequestDAO.create(request);
        for (User user : userDAO.getAll()) {

            // Man skal ikke kunne acceptere sin egen vagt
            if (user.getId() != worker.getId()) {

                Response response = Response.builder()
                        .user(user)
                        .shiftRequest(request)
                        .build();
                response.setStatus(ShiftStatus.NO_RESPONSE);

                responseDAO.create(response);
            }
        }

        // ========================================================
        // HOLIDAYS
        // ========================================================

        Holiday holiday = Holiday.builder()
            .user(worker)
            .startDate(LocalDate.now().plusDays(7))
            .endDate(LocalDate.now().plusDays(10))
            .build();

        holiday.approve();
        holidayDAO.create(holiday);

        // ========================================================
        // ANNOUNCEMENTS
        // ========================================================

        Announcement announcement = Announcement.builder()
            .author(admin)
            .title("Welcome")
            .content("System is ready for testing!")
            .lastUpdated(LocalDateTime.now())
            .build();

        announcementDAO.create(announcement);

    }
}
