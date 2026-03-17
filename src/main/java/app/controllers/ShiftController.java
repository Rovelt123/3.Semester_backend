package app.controllers;

import app.Main;
import app.controllers.Generic.BaseController;
import app.daos.ShiftDAO;
import app.daos.UserDAO;
import app.dtos.ShiftDTO;
import app.entities.Shift;
import app.entities.User;
import app.enums.Notifications;
import app.enums.Role;
import app.services.HolidayAPIService;
import app.services.MessageService;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.Context;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static io.javalin.apibuilder.ApiBuilder.*;

public class ShiftController extends BaseController<Shift, ShiftDTO> {

    private static final ShiftDAO shiftDAO = Main.setup.getShiftDAO();
    private static final UserDAO userDAO = Main.setup.getUserDAO();
    private static final HolidayAPIService holidayService = new HolidayAPIService();

    public ShiftController() {
        super(Shift.class, ShiftDTO::new);
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

        };
    }

    //________________________________________________________

    private void createShift(Context ctx) {

        User admin = ctx.attribute("user");

        if (admin == null || admin.getRoles().stream().anyMatch(role -> role.equals(Role.CHEF))) {
            ctx.status(403).json(Notifications.ADMINS_ONLY.getDisplayName());
            return;
        }

        Map<String,String> body = ctx.bodyAsClass(Map.class);
        int userId = Integer.parseInt(body.get("user_id"));
        String title = body.get("title");

        LocalDate date = LocalDate.parse(body.get("date"));
        LocalTime start = LocalTime.parse(body.get("start_time"));
        LocalTime end = LocalTime.parse(body.get("end_time"));

        User owner = userDAO.getById(userId);
        System.out.println(owner.getFirstname());
        if (owner == null) {
            ctx.status(404).json("User not found for id " + userId);
            return;
        }
        Shift shift = new Shift(title, owner, date, start, end);

        if (holidayService.isHoliday(date)) {
            shift.setTitle("FRI: " + holidayService.getHoliday(date));
        }

        shiftDAO.create(shift);

        String message = MessageService.buildMessage(
                Notifications.SHIFT_CREATED,
                owner.getFirstname(),
                date.toString(),
                start.toString(),
                end.toString()
        );

        ctx.status(201).json(Map.of(
                "message", message,
                "shift", new ShiftDTO(shift)
        ));
    }

    //________________________________________________________

    private void updateShift(Context ctx) {

        User admin = ctx.attribute("user");

        if (admin == null || admin.getRoles().stream().anyMatch(role -> role.equals(Role.CHEF))) {
            ctx.status(403).json(Notifications.ADMINS_ONLY.getDisplayName());
            return;
        }

        int id = Integer.parseInt(ctx.pathParam("id"));

        Shift shift = shiftDAO.getById(id);

        if (shift == null) {
            ctx.status(404).json(Notifications.SHIFT_NOT_FOUND.getDisplayName());
            return;
        }

        Map<String,String> body = ctx.bodyAsClass(Map.class);

        if(body.containsKey("date"))
            shift.setDate(LocalDate.parse(body.get("date")));

        if(body.containsKey("start_time"))
            shift.setStartTime(LocalTime.parse(body.get("start_time")));

        if(body.containsKey("end_time"))
            shift.setEndTime(LocalTime.parse(body.get("end_time")));

        if(body.containsKey("title"))
            shift.setTitle(body.get("title"));

        if (holidayService.isHoliday(shift.getDate())) {
            shift.setTitle("FRI: " + holidayService.getHoliday(shift.getDate()));
        }

        shiftDAO.update(shift);

        String message = MessageService.buildMessage(
                Notifications.SHIFT_UPDATED,
                String.valueOf(id)
        );

        ctx.json(Map.of(
                "message", message,
                "shift", new ShiftDTO(shift)
        ));
    }

    //________________________________________________________

    private void deleteShift(Context ctx) {

        User admin = ctx.attribute("user");

        if (admin == null || admin.getRoles().stream().anyMatch(role -> role.equals(Role.CHEF))) {
            ctx.status(403).json(Notifications.ADMINS_ONLY.getDisplayName());
            return;
        }

        int id = Integer.parseInt(ctx.pathParam("id"));

        shiftDAO.deleteById(id);

        String message = MessageService.buildMessage(
                Notifications.SHIFT_DELETED,
                String.valueOf(id)
        );

        ctx.json(message);
    }

    //________________________________________________________

    private void getShiftsByUser(Context ctx) {

        int userId = Integer.parseInt(ctx.pathParam("id"));

        List<ShiftDTO> shifts = shiftDAO.getShiftsByUserId(userId)
                .stream()
                .map(ShiftDTO::new)
                .toList();

        ctx.json(shifts);
    }

    //________________________________________________________

    private void getShiftsByDate(Context ctx) {

        LocalDate date = LocalDate.parse(ctx.pathParam("date"));

        List<ShiftDTO> shifts = shiftDAO.getShiftsByDate(date)
                .stream()
                .map(ShiftDTO::new)
                .toList();

        ctx.json(shifts);
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

}