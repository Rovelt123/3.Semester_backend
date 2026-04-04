package app.controllers;

import app.controllers.Generic.BaseController;
import app.dtos.ShiftDTO;
import app.dtos.schedule.DayScheduleDTO;
import app.dtos.schedule.ScheduleDTO;
import app.entities.Shift;
import app.entities.User;
import app.enums.Notifications;
import app.enums.Role;
import app.services.TryCatchService;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.Context;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import static io.javalin.apibuilder.ApiBuilder.*;

public class ShiftController extends BaseController<Shift, ShiftDTO> {

    public ShiftController() {
        super(Shift.class, shiftMapper);
    }

    //________________________________________________________

    public static EndpointGroup registerRoutes() {

        ShiftController controller = new ShiftController();
        return () -> {
            post("/shift", controller::createShift, Role.CHEF);
            put("/shift/{id}", controller::updateShift, Role.CHEF);
            delete("/shift/{id}", controller::deleteShift, Role.CHEF);

            get("/shifts", controller::getAll, Role.USER);
            get("/shift/{id}", controller::getByID, Role.USER);
            get("/shifts/date/{date}", controller::getShiftsByDate, Role.USER);
            get("/shifts/user/{id}", controller::getShiftsByUser, Role.USER);

            post("/shift/schedule", controller::scheduleUser, Role.CHEF);

        };
    }

    //________________________________________________________

    private void createShift(Context ctx) {
        Map<String,String> body = TryCatchService.tryBodyMap(ctx, Notifications.BODY_EMPTY.getDisplayName());
        int userId = TryCatchService.tryParseInt(body.get("user_id"), Notifications.MUST_BE_INT.getDisplayName());
        String title = TryCatchService.tryString(body.get("title"), Notifications.MUST_ENTER_TITLE.getDisplayName());

        LocalDate date = TryCatchService.tryParseLocalDate(
            body.get("date"),
            messageService.buildMessage(
                Notifications.MUST_BE_DATE_FORMAT,
                body.get("date")
            )
        );

        LocalTime start = TryCatchService.tryParseLocalTime(
            body.get("start_time"),
            messageService.buildMessage(
                Notifications.MUST_BE_TIME_FORMAT,
                body.get("start_time")
            )
        );

        LocalTime end = TryCatchService.tryParseLocalTime(
            body.get("end_time"),
            messageService.buildMessage(
                Notifications.MUST_BE_TIME_FORMAT,
                body.get("end_time")
            )
        );

        User owner = TryCatchService.tryEntity(
            userDAO.getById(userId),
            messageService.buildMessage(
                Notifications.USER_NOT_FOUND_ID,
                String.valueOf(userId)
            )
        );

        Shift shift = Shift.builder()
            .title(title)
            .owner(owner)
            .date(date)
            .startTime(start)
            .endTime(end)
            .build();

        Map<LocalDate, String> holidays = holidayService.getHolidays(date.getYear());
        if (holidayService.isHoliday(date, holidays)) {
            shift.setTitle("FRI: " + holidayService.getHoliday(date, holidays));
        }

        shiftDAO.create(shift);

        String message = messageService.buildMessage(
            Notifications.SHIFT_CREATED,
            owner.getFirstname(),
            date.toString(),
            start.toString(),
            end.toString()
        );

        respond(ctx, 201, message, Map.of(
            "data", shiftMapper.toDTO(shift)
        ));
    }

    //________________________________________________________

    private void updateShift(Context ctx) {
        int id = getPathId(ctx);

        Shift shift = TryCatchService.tryEntity(
            shiftDAO.getById(id),
            messageService.buildMessage(
                Notifications.NOT_FOUND_ID,
                "Shift",
                String.valueOf(id)
            )
        );

        Map<String,String> body = TryCatchService.tryBodyMap(
            ctx,
            Notifications.BODY_EMPTY.getDisplayName()
        );

        if(body.containsKey("date"))
            shift.setDate(TryCatchService.tryParseLocalDate(
                body.get("date"),
                Notifications.MUST_BE_DATE_FORMAT.getDisplayName()
            ));

        if(body.containsKey("start_time"))
            shift.setStartTime(TryCatchService.tryParseLocalTime(
                body.get("start_time"),
                Notifications.MUST_BE_TIME_FORMAT.getDisplayName()
            ));

        if(body.containsKey("end_time"))
            shift.setEndTime(TryCatchService.tryParseLocalTime(
                body.get("end_time"),
                Notifications.MUST_BE_TIME_FORMAT.getDisplayName()
            ));

        if(body.containsKey("title"))
            shift.setTitle(TryCatchService.tryString(
                body.get("title"),
                Notifications.MUST_ENTER_TITLE.getDisplayName()
            ));

        if(body.containsKey("owner")) {
            Integer userId = TryCatchService.tryParseInt(
                body.get("owner"),
                Notifications.MUST_BE_INT.getDisplayName()
            );
            User newOwner = TryCatchService.tryEntity(
                userDAO.getById(userId),
                messageService.buildMessage(
                    Notifications.USER_NOT_FOUND_ID,
                    String.valueOf(userId)
                )
            );
            shift.setOwner(newOwner);
        }

        Map<LocalDate, String> holidays = holidayService.getHolidays(shift.getDate().getYear());
        if(holidayService.isHoliday(shift.getDate(), holidays))
            shift.setTitle("FRI: " + holidayService.getHoliday(shift.getDate(), holidays));


        shiftDAO.update(shift);

        String message = messageService.buildMessage(
            Notifications.SHIFT_UPDATED,
            String.valueOf(id)
        );

        respond(ctx, 200, message, Map.of(
            "data", shiftMapper.toDTO(shift)
        ));
    }

    //________________________________________________________

    private void deleteShift(Context ctx) {

        int id = getPathId(ctx);

        Shift shift  = TryCatchService.tryEntity(shiftDAO.getById(id), messageService.buildMessage(
                Notifications.SHIFT_NOT_FOUND,
                String.valueOf(id)
            )
        );
        shiftDAO.delete(shift);

        String message = messageService.buildMessage(
            Notifications.SHIFT_DELETED,
            String.valueOf(id)
        );

        respond(ctx, 200, message, null);
    }

    //________________________________________________________

    private void getShiftsByUser(Context ctx) {

        int userId = getPathId(ctx);

        List<ShiftDTO> shifts = shiftDAO.getShiftsByUserId(userId)
            .stream()
            .map(shiftMapper::toDTO)
            .toList();

        if(shifts.isEmpty()) {
            ctx.status(200).json(messageService.buildMessage(
                Notifications.NO_SHIFTS,
                String.valueOf(userId)
            ));
            return;
        }

        String message = messageService.buildMessage(
            Notifications.GET_BY_USER,
            "shift",
            String.valueOf(userId)
        );

        respond(ctx, 200, message, Map.of(
            "data", shifts
        ));
    }

    //________________________________________________________

    private void getShiftsByDate(Context ctx) {

        LocalDate date = TryCatchService.tryParseLocalDate(
            ctx.pathParam("date"),
            messageService.buildMessage(
                Notifications.MUST_BE_DATE_FORMAT,
                ctx.pathParam("date")
            )
        );

        List<ShiftDTO> shifts = shiftDAO.getShiftsByDate(date)
            .stream()
            .map(shiftMapper::toDTO)
            .toList();

        if(shifts.isEmpty()) {

            String message = messageService.buildMessage(
                Notifications.GET_BY_DATE_EMPTY,
                String.valueOf(date)
            );

            respond(ctx, 200, message, null);
            return;

        }

        String message = messageService.buildMessage(
            Notifications.GET_SHIFTREQUEST_DATE,
            String.valueOf(shifts.size()),
            String.valueOf(date)
        );

        respond(ctx, 200, message, Map.of("data", shifts));
    }

    //________________________________________________________

    @Override
    protected List<Shift> getAllEntities() {
        return shiftDAO.getAll();
    }

    //________________________________________________________

    @Override
    protected Shift getEntityById(int id) {
        return shiftDAO.getById(id);
    }

    //________________________________________________________

    private void scheduleUser(Context ctx) {

        ScheduleDTO schedule = ctx.bodyAsClass(ScheduleDTO.class);

        int userId = schedule.getUser_id();

        User user = TryCatchService.tryEntity(
            userDAO.getById(userId),
            messageService.buildMessage(
                Notifications.USER_NOT_FOUND_ID,
                String.valueOf(userId)
            )
        );

        threadService.runAsync(() -> runSchedule(user, schedule));

        String message = messageService.buildMessage(
            Notifications.SCHEDULE_CREATED,
            user.getUsername(),
            convertToTime(schedule.getMonday()),
            convertToTime(schedule.getTuesday()),
            convertToTime(schedule.getWednesday()),
            convertToTime(schedule.getThursday()),
            convertToTime(schedule.getFriday()),
            convertToTime(schedule.getSaturday()),
            convertToTime(schedule.getSunday())
        );

        respond(ctx, 201, message, null);
    }

    //________________________________________________________

    private void runSchedule(User user, ScheduleDTO schedule) {

        int months = schedule.getMonths();

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusMonths(months);

        Map<LocalDate, String> holidays = holidayService.getHolidays(startDate.getYear());

        LocalDate current = startDate;

        while (!current.isAfter(endDate)) {

            DayScheduleDTO day = switch (current.getDayOfWeek()) {
                case MONDAY -> schedule.getMonday();
                case TUESDAY -> schedule.getTuesday();
                case WEDNESDAY -> schedule.getWednesday();
                case THURSDAY -> schedule.getThursday();
                case FRIDAY -> schedule.getFriday();
                case SATURDAY -> schedule.getSaturday();
                case SUNDAY -> schedule.getSunday();
            };

            boolean isHoliday = holidayService.isHoliday(current, holidays);

            Shift existingShift = shiftDAO.findByUserAndDate(user.getId(), current);

            if (!day.isOffDay() && !isHoliday) {

                LocalTime start = TryCatchService.tryParseLocalTime(
                        day.getStart_time(),
                        Notifications.MUST_BE_TIME_FORMAT.getDisplayName()
                );

                LocalTime end = TryCatchService.tryParseLocalTime(
                        day.getEnd_time(),
                        Notifications.MUST_BE_TIME_FORMAT.getDisplayName()
                );

                if (existingShift != null) {
                    existingShift.setTitle(day.getTitle());
                    existingShift.setStartTime(start);
                    existingShift.setEndTime(end);
                    shiftDAO.update(existingShift);
                } else {
                    shiftDAO.create(Shift.builder()
                        .title(day.getTitle())
                        .owner(user)
                        .date(current)
                        .startTime(start)
                        .endTime(end)
                        .build());
                }

            } else if (isHoliday) {

                String title = "FRI: " + holidayService.getHoliday(current, holidays);

                if (existingShift != null) {
                    existingShift.setTitle(title);
                    existingShift.setStartTime(null);
                    existingShift.setEndTime(null);
                    shiftDAO.update(existingShift);
                } else {
                    shiftDAO.create(Shift.builder()
                            .title(title)
                            .owner(user)
                            .date(current)
                            .build());
                }
            }

            current = current.plusDays(1);
        }
    }

    //________________________________________________________

    private String convertToTime(DayScheduleDTO scheduleDTO) {
        return scheduleDTO.getStart_time() + " - " + scheduleDTO.getEnd_time();
    }

}