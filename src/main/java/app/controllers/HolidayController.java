package app.controllers;

import app.Main;
import app.controllers.Generic.BaseController;
import app.daos.HolidayDAO;
import app.daos.UserDAO;
import app.dtos.HolidayDTO;
import app.entities.Holiday;
import app.entities.User;
import app.enums.HolidayStatus;
import app.enums.Notifications;
import app.enums.Role;

import app.enums.ShiftStatus;
import app.services.MessageService;
import app.services.TryCatchService;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.Context;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static io.javalin.apibuilder.ApiBuilder.*;

public class HolidayController extends BaseController<Holiday, HolidayDTO> {

    private static final HolidayDAO holidayDAO = Main.setup.getHolidayDAO();
    private static final UserDAO userDAO = Main.setup.getUserDAO();

    //________________________________________________________

    public HolidayController(){
        super(Holiday.class, HolidayDTO::new);
    }

    //________________________________________________________

    public static EndpointGroup registerRoutes(){

        HolidayController controller = new HolidayController();

        return () -> {
            post("/holiday", controller::requestHoliday, Role.USER);
            put("/holiday/{id}", controller::updateHoliday, Role.USER);
            post("/holiday/{id}/approve", controller::approveHoliday, Role.CHEF);
            post("/holiday/{id}/reject", controller::rejectHoliday, Role.CHEF);

            get("/holidays", controller::getAll, Role.USER);
            get("/holiday/{id}", controller::getByID, Role.USER);
            get("/holidays/responsibility/{name}", controller::getHolidayByResponsibilities, Role.USER);
        };
    }

    //________________________________________________________

    private void requestHoliday(Context ctx){

        User user = getAuthenticatedUser(ctx);

        Map<String,String> body = TryCatchService.tryBodyMap(
                ctx,
                Notifications.BODY_EMPTY.getDisplayName()
        );

        LocalDate start = TryCatchService.tryParseLocalDate(
            body.get("start"),
            Notifications.MUST_BE_DATE_FORMAT.getDisplayName()
        );

        LocalDate end = TryCatchService.tryParseLocalDate(
            body.get("end"),
            Notifications.MUST_BE_DATE_FORMAT.getDisplayName()
        );

        Holiday holiday = Holiday.builder()
            .user(user)
            .startDate(start)
            .endDate(end)
            .build();

        holidayDAO.create(holiday);

        String message = MessageService.buildMessage(
            Notifications.CREATED,
            "Message"
        );

        respond(ctx, 201, message, Map.of("data", new HolidayDTO(holiday)));
    }

    //________________________________________________________

    private void updateHoliday(Context ctx){

        User user = getAuthenticatedUser(ctx);

        int id = getPathId(ctx);

        Holiday holiday = TryCatchService.tryEntity(
            holidayDAO.getById(id),
            MessageService.buildMessage(
                Notifications.GET_BY_ID,
                "holiday",
                String.valueOf(id)
            )
        );

        if(holiday.getUser().getId() != user.getId() && !user.getRoles().contains(Role.CHEF)){
            respond(ctx, 403, Notifications.NOT_ALLOWED.getDisplayName(), null);
            return;
        }

        Map<String,String> body = TryCatchService.tryBodyMap(
            ctx,
            Notifications.BODY_EMPTY.getDisplayName()
        );

        if (body.containsKey("start"))
            holiday.setStartDate(TryCatchService.tryParseLocalDate(
                    body.get("start"),
                    Notifications.MUST_BE_DATE_FORMAT.getDisplayName()
            ));

        if (body.containsKey("end"))
            holiday.setEndDate(TryCatchService.tryParseLocalDate(
                    body.get("end"),
                    Notifications.MUST_BE_DATE_FORMAT.getDisplayName()
            ));

        if (body.containsKey("owner") && user.getRoles().contains(Role.CHEF)) {
            int userID = TryCatchService.tryParseInt(
                body.get("owner"),
                Notifications.MUST_BE_INT.getDisplayName()
            );

            holiday.setUser(TryCatchService.tryEntity(
                userDAO.getById(userID),
                MessageService.buildMessage(
                    Notifications.NOT_FOUND_ID,
                    "User",
                    body.get("owner")
                )
            ));
        }


        if (body.containsKey("status") && user.getRoles().contains(Role.CHEF)) {
            HolidayStatus status = TryCatchService.tryParseEnum(
                HolidayStatus.class,
                body.get("status"),
                Notifications.ENUM_NOT_FOUND.getDisplayName()
            );

            holiday.setStatus(status);
        }

        holidayDAO.update(holiday);

        String message = MessageService.buildMessage(
            Notifications.UPDATED,
            "Holiday"
        );

        respond(ctx, 200, message, Map.of("data", new HolidayDTO(holiday)));
    }

    //________________________________________________________

    private void approveHoliday(Context ctx){

        int id = getPathId(ctx);

        Holiday holiday = TryCatchService.tryEntity(
            holidayDAO.getById(id),
            MessageService.buildMessage(
                Notifications.NOT_FOUND_ID,
                "holiday",
                String.valueOf(id)
            )
        );

        holiday.approve();

        holidayDAO.update(holiday);

        respond(ctx, 200, Notifications.HOLIDAY_APPROVED.getDisplayName(), Map.of("data", new HolidayDTO(holiday)));
    }

    //________________________________________________________

    private void rejectHoliday(Context ctx){

        int id = getPathId(ctx);

        Holiday holiday = TryCatchService.tryEntity(
            holidayDAO.getById(id),
            MessageService.buildMessage(
                Notifications.NOT_FOUND_ID,
                    "holiday",
                String.valueOf(id)
            )
        );

        holiday.reject();

        holidayDAO.update(holiday);

        respond(ctx, 200, Notifications.HOLIDAY_REJECT.getDisplayName(), Map.of("data", new HolidayDTO(holiday)));
    }

    //________________________________________________________

    @Override
    protected List<Holiday> getAllEntities() {
        return holidayDAO.getAll();
    }

    //________________________________________________________

    @Override
    protected Holiday getEntityById(int id) {
        return holidayDAO.getById(id);
    }

    //________________________________________________________

    private void getHolidayByResponsibilities(Context ctx){

        String name = ctx.pathParam("name");

        List<HolidayDTO> holidays = holidayDAO.getAll()
            .stream()
            .filter(h -> h.getUser().getResponsibilities()
            .stream()
            .anyMatch(r -> r.getName().equalsIgnoreCase(name)))
            .map(HolidayDTO::new)
            .toList();


        if(holidays.isEmpty()){
            String message = MessageService.buildMessage(
                    Notifications.HOLIDAY_EMPTY_RESPONSIBILITY,
                    name
            );

            respond(ctx, 200, message, null);
            return;
        }

        String message = MessageService.buildMessage(
            Notifications.GET_ALL,
            String.valueOf(holidays.size()),
            "Holiday"
        );

        respond(ctx, 200, message, Map.of("data", holidays));
    }
}