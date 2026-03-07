package app.controllers;

import app.Main;
import app.daos.ShiftDAO;
import app.daos.UserDAO;
import app.dtos.ShiftDTO;
import app.dtos.schedule.DayScheduleDTO;
import app.dtos.schedule.ScheduleDTO;
import app.entities.Shift;
import app.entities.User;
import app.enums.Notifications;
import app.services.HolidayAPIService;
import app.services.MessageService;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ShiftController {

    private static final HolidayAPIService holidayService = new HolidayAPIService();
    private static final ShiftDAO shiftDao = Main.setup.getShiftDAO();
    private static final UserDAO userDao = Main.setup.getUserDAO();

    // ________________________________________________________


    public static void registerRoutes(Javalin app) {

        //POST
        app.post("/shifts", ShiftController::createShift);
        app.post("/schedules", ShiftController::createSchedule);

        //DELETE
        app.delete("/shifts/{id}", ShiftController::deleteShift);

        //PUT
        app.put("/shifts", ShiftController::updateShift);

        //GET
        app.get("/shifts", ShiftController::getAll);
        app.get("/shifts/{id}", ShiftController::getByID);
        app.get("/shifts/user/{user_id}", ShiftController::getShiftsByUserID);
        app.get("/shifts/date/{date}", ShiftController::getShiftsByDate);
    }

    // ________________________________________________________

    private static void createShift(Context ctx) {
        ShiftDTO shift = createShift(
                ctx,
                ctx.pathParam("user_id"),
                ctx.pathParam("title"),
                ctx.pathParam("date"),
                ctx.pathParam("start_time"),
                ctx.pathParam("end_time")
        );


        String message = MessageService.buildMessage(
            Notifications.SHIFT_CREATED,
            shift.getUser().getName(),
            shift.getDate().toString(),
            shift.getStartTime().toString(),
            shift.getEndTime().toString()
        );

        MessageService.notify(message);

        ctx.status(201).json(Map.of(
        "message", message,
        "shift", shift
        ));
    }

    // ________________________________________________________

    //TODO: Skal lave, så man kan planlægge:
    // 1.) Så man kan ligge dage ind som fri
    // 2.) Have forskellige mødetidspunkter på forskellige dage
    // 3.) Indsætte perioden for skema planlægningen ind - Eksempelvis, at den skal
    // oprette vagter automatisk for 6 månder frem.
    private static void createSchedule(Context ctx) {
        ScheduleDTO schedule = ctx.bodyAsClass(ScheduleDTO.class);

        User user = userDao.getById(schedule.getUser_id());
        int months = schedule.getMonths();

        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusMonths(months);

        List<ShiftDTO> createdShifts = new ArrayList<>();

        for(LocalDate date = today; date.isBefore(endDate); date = date.plusDays(1)) {

            DayScheduleDTO day = switch (date.getDayOfWeek()) {

                case MONDAY -> schedule.getMonday();
                case TUESDAY -> schedule.getTuesday();
                case WEDNESDAY -> schedule.getWednesday();
                case THURSDAY -> schedule.getThursday();
                case FRIDAY -> schedule.getFriday();
                case SATURDAY -> schedule.getSaturday();
                case SUNDAY -> schedule.getSunday();

            };

            if(day == null || day.isOffDay()) {
                continue;
            }

            ShiftDTO shift = createShift(
                    ctx,
                    String.valueOf(user.getId()),
                    day.getTitle(),
                    date.toString(),
                    day.getStart_time(),
                    day.getEnd_time()
            );

            if(shift != null){
                createdShifts.add(shift);
            }
        }

        String message = MessageService.buildMessage(
            Notifications.SCHEDULE_CREATED,
            user.getName(),
            formatDay(schedule.getMonday()),
            formatDay(schedule.getTuesday()),
            formatDay(schedule.getWednesday()),
            formatDay(schedule.getThursday()),
            formatDay(schedule.getFriday()),
            formatDay(schedule.getSaturday()),
            formatDay(schedule.getSunday())
        );

        MessageService.notify(message);

        ctx.status(201).json(Map.of(
        "message", message,
        "schedule", createdShifts
        ));
    }

    // ________________________________________________________

    private static void deleteShift(Context ctx) {
        String entered = ctx.pathParam("id");
        System.out.println(entered);
        try {
            int id = Integer.parseInt(entered);

            shiftDao.deleteById(id);

            String message = MessageService.buildMessage(
                    Notifications.SHIFT_DELETED,
                    String.valueOf(id)
            );

            MessageService.notify(message);

            ctx.status(204);

        } catch (NumberFormatException e) {
            String message = MessageService.buildMessage(
                    Notifications.MUST_BE_INT,
                    entered
            );

            MessageService.sendError(message);
            ctx.status(400).json(message);
        }
    }

    // ________________________________________________________
    //TODO: Insert values to be updated??
    private static void updateShift(Context ctx) {
        String id = ctx.pathParam("id");
        try {
            Shift shift = shiftDao.getById(Integer.parseInt(id));

            Shift updated = shiftDao.update(shift);

            if (holidayService.isHoliday(updated.getDate())) {
                String holiday = holidayService.getHoliday(updated.getDate());
                updated.setTitle("FRI: " + holiday);
                shiftDao.update(updated);
            }

            String message = MessageService.buildMessage(
                    Notifications.SHIFT_UPDATED,
                    String.valueOf(updated.getId()),
                    updated.getOwner().getName(),
                    updated.getTitle(),
                    updated.getDate().toString(),
                    updated.getStartTime().toString(),
                    updated.getEndTime().toString()
            );

            MessageService.notify(message);

            ctx.status(201).json(convertDTO(updated));

        } catch (NumberFormatException e) {
            String message = MessageService.buildMessage(Notifications.MUST_BE_INT, id);
            MessageService.notify(message);
            ctx.status(500).json(message);
        }
    }

    // ________________________________________________________

    private static void getAll(Context ctx) {
        List<ShiftDTO> shifts = new ArrayList<>();

        shiftDao.getAll().forEach(shift -> {
           shifts.add(convertDTO(shift));
        });

        String message = MessageService.buildMessage(
                Notifications.SHIFT_GET_ALL,
                String.valueOf(shifts.size())
        );
        MessageService.notify(message);

        ctx.status(201).json(shifts);
    }

    // ________________________________________________________

    private static void getByID(Context ctx) {

        try {
            int id = Integer.parseInt(ctx.pathParam("id"));

            Shift shift = shiftDao.getById(id);
            if (shift == null) {
                ctx.status(404);
                return;
            }
            String message = MessageService.buildMessage(Notifications.SHIFT_GET_BY_ID, String.valueOf(id));
            MessageService.notify(message);
            ctx.status(200).json(Map.of(
                    "Data", convertDTO(shift),
                    "Message", message
            ));
        } catch (NumberFormatException e) {
            MessageService.sendError(
                    MessageService.buildMessage(Notifications.MUST_BE_INT, ctx.pathParam("id"))
            );
            ctx.status(400);
        }
    }

    // ________________________________________________________

    private static void getShiftsByUserID(Context ctx) {

        String userId = ctx.pathParam("user_id");

        List<ShiftDTO> shifts = new ArrayList<>();

        shiftDao.getShiftsByUserId(userId).forEach(shift -> {
            shifts.add(convertDTO(shift));
        });

        ctx.status(200).json(shifts);
    }

    // ________________________________________________________

    private static void getShiftsByDate(Context ctx) {

        try {

            LocalDate date = LocalDate.parse(ctx.pathParam("date"));

            List<ShiftDTO> shifts = new ArrayList<>();

            shiftDao.getShiftsByDate(date).forEach(shift -> {
                shifts.add(convertDTO(shift));
            });

            ctx.status(200).json(shifts);

        } catch (DateTimeParseException e) {

            String message = MessageService.buildMessage(
                    Notifications.MUST_BE_DATE_FORMAT,
                    ctx.pathParam("date")
            );

            MessageService.sendError(message);
            ctx.status(400).json(message);
        }
    }

    // ________________________________________________________

    private static ShiftDTO convertDTO(Shift shift) {
        return new ShiftDTO(shift);
    }

    // ________________________________________________________

    private static ShiftDTO createShift(Context ctx, String id, String title, String sDate, String sStartTime, String sEndTime) {

        try {
            User user = userDao.getById(id);
            LocalDate date = LocalDate.parse(sDate);
            LocalTime startTime = LocalTime.parse(sStartTime);
            LocalTime endTime = LocalTime.parse(sEndTime);

            Shift shift = new Shift(title, user, date, startTime, endTime);

            if (holidayService.isHoliday(shift.getDate())) {
                String holiday = holidayService.getHoliday(shift.getDate());
                shift.setTitle("FRI: " + holiday);
            }

            Shift created = shiftDao.create(shift);

            return convertDTO(created);
        } catch (NumberFormatException e) {
            String message = MessageService.buildMessage(Notifications.MUST_BE_INT, id);
            MessageService.notify(message);
            ctx.status(500).json(message);
        } catch (DateTimeParseException e) {
            String message = MessageService.buildMessage(Notifications.MUST_BE_DATE_FORMAT, sDate);
            MessageService.notify(message);
            ctx.status(500).json(message);
        }
        return null;
    }

    // ________________________________________________________

    private static String formatDay(DayScheduleDTO day){

        if(day == null || day.isOffDay()){
            return "FRI";
        }

        return day.getStart_time() + "-" + day.getEnd_time();
    }

}
