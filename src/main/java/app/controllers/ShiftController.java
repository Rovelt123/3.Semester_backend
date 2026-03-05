package app.controllers;

import app.Main;
import app.daos.ShiftDAO;
import app.dtos.ShiftDTO;
import app.entities.Shift;
import app.enums.Notifications;
import app.services.HolidayAPIService;
import app.services.MessageService;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;
import java.util.Map;


public class ShiftController {

    private static final HolidayAPIService holidayService = new HolidayAPIService();
    private static final ShiftDAO shiftDao = Main.setup.getShiftDAO();

    // ________________________________________________________


    public static void registerRoutes(Javalin app) {
        app.post("/shifts", ShiftController::createShift);
        app.delete("/shifts/{id}", ShiftController::deleteShift);
        app.put("/shifts", ShiftController::updateShift);
        app.post("/updateHolidays", ShiftController::updateHolidays);
        app.get("/shifts", ShiftController::getAll);
        app.get("/shifts/{id}", ShiftController::getByID);
    }

    // ________________________________________________________

    public static void createShift(Context ctx) {

        Shift shift = ctx.bodyAsClass(Shift.class);

        if (holidayService.isHoliday(shift.getDate())) {
            String holiday = holidayService.getHoliday(shift.getDate());
            shift.setTitle("FRI: " + holiday);
        }

        Shift created = shiftDao.create(shift);

        String message = MessageService.buildMessage(
                Notifications.SHIFT_CREATED,
                created.getOwner().getName(),
                created.getDate().toString(),
                created.getStartTime().toString(),
                created.getEndTime().toString()
        );

        MessageService.notify(message);

        ctx.status(201).json(created);
    }

    // ________________________________________________________

    public static void deleteShift(Context ctx) {
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

    public static void updateShift(Context ctx) {

        try {
            Shift shift = ctx.bodyAsClass(Shift.class);

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

            ctx.status(201).json(updated);

        } catch (Exception e) {
            ctx.status(400).json(e.getMessage());
        }
    }

    // ________________________________________________________

    public static void updateHolidays(Context ctx) {
        String entered = ctx.pathParam("shift_id");
        try {
            int id = Integer.parseInt(entered);
            Shift shift = shiftDao.getById(id);

            if (holidayService.isHoliday(shift.getDate())) {
                String holiday = holidayService.getHoliday(shift.getDate());
                shift.setTitle("FRI: " + holiday);
            }

            Shift created = shiftDao.create(shift);

            String message = MessageService.buildMessage(
                    Notifications.SHIFT_CREATED,
                    created.getOwner().getName(),
                    created.getDate().toString(),
                    created.getStartTime().toString(),
                    created.getEndTime().toString()
            );
            MessageService.notify(message);
            ctx.status(201).json(created);
        } catch (Exception e) {
            String message = MessageService.buildMessage(Notifications.MUST_BE_INT, entered);
            MessageService.sendError(message);
            ctx.status(400).json(message);
        }

    }

    // ________________________________________________________

    public static void getAll(Context ctx) {
        List<Shift> shifts = shiftDao.getAll();

        String message = MessageService.buildMessage(
                Notifications.SHIFT_GET_ALL,
                String.valueOf(shifts.size())
        );
        MessageService.notify(message);

        ctx.status(201).json(shifts);
    }

    // ________________________________________________________

    public static void getByID(Context ctx) {

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


    public static ShiftDTO convertDTO(Shift shift) {
        return new ShiftDTO(shift);
    }

}
