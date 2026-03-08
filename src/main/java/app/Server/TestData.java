package app.Server;

import app.Main;
import app.daos.*;
import app.entities.*;
import app.enums.Responsibilities;
import app.enums.Role;
import app.enums.ShiftStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public class TestData {

    public static void generate() {
        ShiftDAO shiftDao = Main.setup.getShiftDAO();
        UserDAO userDAO = Main.setup.getUserDAO();
        ResponsibilityDAO responsibilityDAO = Main.setup.getRespDAO();
        ShiftRequestDAO shiftRequestDAO = Main.setup.getShiftRequestDAO();
        AnnouncementDAO announcementDAO = Main.setup.getAnnouncementDAO();
        MessageDAO messageDAO = Main.setup.getMessageDAO();
        HolidayDAO holidayDAO = Main.setup.getHolidayDAO();
        ResponseDAO responseDAO = Main.setup.getResponseDAO();

        // ========================================================
        // USERS
        // ========================================================

        User admin = new User("Admin User", Role.CHEF);
        User worker = new User("Worker User", Role.USER);
        User worker2 = new User("Worker User", Role.USER);
        User worker3 = new User("Worker User", Role.USER);
        User worker4 = new User("Worker User", Role.USER);

        userDAO.create(admin);
        userDAO.create(worker);
        userDAO.create(worker2);
        userDAO.create(worker3);
        userDAO.create(worker4);

        // ========================================================
        // RESPONSIBILITIES
        // ========================================================

        for (Responsibilities r : Responsibilities.values()) {

            Responsibility entity = new Responsibility(r.getDisplayName());

            responsibilityDAO.create(entity);
        }



        worker.addResponsibility(responsibilityDAO.getByName(Responsibilities.CASHIER.getDisplayName()));
        worker.addResponsibility(responsibilityDAO.getByName(Responsibilities.DRIVER.getDisplayName()));
        userDAO.update(worker);
        admin.addResponsibility(responsibilityDAO.getByName(Responsibilities.PLANNER.getDisplayName()));
        userDAO.update(admin);

        // ========================================================
        // SHIFTS
        // ========================================================

        Shift shift1 = new Shift("Morning Shift", worker,
                LocalDate.now(),
                LocalTime.of(8,0),
                LocalTime.of(16,0));

        Shift shift2 = new Shift("Evening Shift", worker,
                LocalDate.now().plusDays(1),
                LocalTime.of(14,0),
                LocalTime.of(22,0));

        shiftDao.create(shift1);
        shiftDao.create(shift2);

        // ========================================================
        // SHIFT REQUESTS (swap / request)
        // ========================================================

        ShiftRequest request = new ShiftRequest(worker, shift1);

        shiftRequestDAO.create(request);
        for (User user : userDAO.getAll()) {

            // Man skal ikke kunne acceptere sin egen vagt
            if (user.getId() != worker.getId()) {

                Response response = new Response(user, request);
                response.setStatus(ShiftStatus.NO_RESPONSE);

                responseDAO.create(response);
            }
        }

        // ========================================================
        // HOLIDAYS
        // ========================================================

        Holiday holiday = new Holiday(worker,
                LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(10));

        holiday.approve();
        holidayDAO.create(holiday);

        // ========================================================
        // ANNOUNCEMENTS
        // ========================================================

        Announcement announcement = new Announcement(admin,
                "Welcome",
                "System is ready for testing");

        announcementDAO.create(announcement);

        // ========================================================
        // MESSAGES
        // ========================================================

        Message message = new Message(worker,
                "Hello manager, I would like to swap shift");

        messageDAO.create(message);
    }
}
