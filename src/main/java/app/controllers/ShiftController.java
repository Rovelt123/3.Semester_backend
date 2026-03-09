package app.controllers;

import app.Main;
import app.controllers.Generic.BaseController;
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


public class ShiftController extends BaseController<Shift, ShiftDTO> {

    private static final HolidayAPIService holidayService = new HolidayAPIService();
    private static final ShiftDAO shiftDao = Main.setup.getShiftDAO();
    private static final UserDAO userDao = Main.setup.getUserDAO();

    // ________________________________________________________

    public ShiftController() {
        super(Shift.class, ShiftDTO::new);
    }

    // ________________________________________________________

    public static void registerRoutes(Javalin app) {

        ShiftController controller = new ShiftController();


        //POST
        app.post("/shift", controller::createShift);
        app.post("/schedule", controller::createSchedule);

        //DELETE
        app.delete("/shift/{id}", controller::deleteShift);

        //PUT
        app.put("/shifts", controller::updateShift);

        //GET
        app.get("/shifts", controller::getAll);
        app.get("/shift/{id}", controller::getByID);
        app.get("/shifts/user/{user_id}", controller::getShiftsByUserID);
        app.get("/shifts/date/{date}", controller::getShiftsByDate);
    }

    // ________________________________________________________

    private void createShift(Context ctx) {
        ShiftDTO shift = createShift(
                ctx,
                ctx.pathParam("user_id"),
                ctx.pathParam("title"),
                ctx.pathParam("date"),
                ctx.pathParam("start_time"),
                ctx.pathParam("end_time")
        );


        assert shift != null;
        String message = MessageService.buildMessage(
            Notifications.SHIFT_CREATED,
            String.valueOf(userDao.getById(shiftDao.getById(shift.getId()).getOwnerID())),
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
    private void createSchedule(Context ctx) {
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

    private void deleteShift(Context ctx) {
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
    private void updateShift(Context ctx) {
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
                    Main.setup.getUserDAO().getById(updated.getOwnerID()).getName(),
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

    @Override
    protected List<Shift> getAllEntities() {
        return shiftDao.getAll();
    }

    // ________________________________________________________

    @Override
    protected Shift getEntityById(int id) {
        return shiftDao.getById(id);
    }

    // ________________________________________________________

    private void getShiftsByUserID(Context ctx) {

        String userId = ctx.pathParam("user_id");

        List<ShiftDTO> shifts = new ArrayList<>();

        shiftDao.getShiftsByUserId(userId).forEach(shift -> shifts.add(convertDTO(shift)));

        ctx.status(200).json(shifts);
    }

    // ________________________________________________________

    private void getShiftsByDate(Context ctx) {

        try {

            LocalDate date = LocalDate.parse(ctx.pathParam("date"));

            List<ShiftDTO> shifts = new ArrayList<>();

            shiftDao.getShiftsByDate(date).forEach(shift -> shifts.add(convertDTO(shift)));

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

    private ShiftDTO convertDTO(Shift shift) {
        return new ShiftDTO(shift);
    }

    // ________________________________________________________

    private ShiftDTO createShift(Context ctx, String id, String title, String sDate, String sStartTime, String sEndTime) {

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

    private String formatDay(DayScheduleDTO day){

        if(day == null || day.isOffDay()){
            return "FRI";
        }

        return day.getStart_time() + "-" + day.getEnd_time();
    }

}
