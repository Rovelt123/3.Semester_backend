package app.controllers.Generic;

import app.Main;
import app.daos.*;
import app.dtos.UserDTO;
import app.entities.Response;
import app.entities.User;
import app.enums.Notifications;
import app.services.HolidayAPIService;
import app.services.Mappers.*;
import app.services.MessageService;
import app.services.ThreadService;
import app.services.TryCatchService;
import app.services.security.SecurityService;
import io.javalin.http.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;



public abstract class BaseController<E, D> implements IController {

    protected Class<E> entityClass;
    protected Mapper<E, D> mapper;
    protected abstract List<E> getAllEntities();
    protected abstract E getEntityById(int id);

    // ________________________________________________________

    protected final MessageService messageService = Main.setup.getMessageService();
    protected final ThreadService threadService = Main.setup.getThreadService();
    protected static final SecurityService securityService = new SecurityService();
    protected static final HolidayAPIService holidayService = new HolidayAPIService();

    // ________________________________________________________

    protected static final ResponseDAO responseDAO = Main.setup.getResponseDAO();
    protected static final ShiftRequestDAO shiftRequestDAO = Main.setup.getShiftRequestDAO();

    protected final UserDAO userDAO = Main.setup.getUserDAO();
    protected final ShiftDAO shiftDAO = Main.setup.getShiftDAO();
    protected final ResponsibilityDAO responsibilityDAO = Main.setup.getRespDAO();
    protected final HolidayDAO holidayDAO = Main.setup.getHolidayDAO();
    protected final AnnouncementDAO announcementDAO = Main.setup.getAnnouncementDAO();

    // ________________________________________________________

    protected final static UserMapper userMapper = new UserMapper();
    protected final static ShiftRequestMapper shiftRequestMapper = new ShiftRequestMapper();
    protected final static ShiftMapper shiftMapper = new ShiftMapper();
    protected final static ResponsibilityMapper responsibilityMapper = new ResponsibilityMapper();
    protected final static ResponseMapper responseMapper = new ResponseMapper();
    protected final static HolidayMapper holidayMapper = new HolidayMapper();
    protected final static AnnouncementMapper announcementMapper = new AnnouncementMapper();

    // ________________________________________________________

    protected BaseController(Class<E> entityClass, Mapper<E, D> mapper) {
        this.entityClass = entityClass;
        this.mapper = mapper;
    }

    // ________________________________________________________

    @Override
    public void getAll(Context ctx) {

        List<D> list = new ArrayList<>();

        if (getAllEntities().isEmpty()) {
            String message = messageService.buildMessage(Notifications.GET_ALL_EMPTY, entityClass.getSimpleName().toLowerCase(Locale.ROOT));
            respond(ctx, 200, message, null);
            return;
        }

        getAllEntities().forEach(entity -> list.add(mapper.toDTO(entity)));


        String message = messageService.buildMessage(
                Notifications.GET_ALL,
                String.valueOf(list.size()),
                entityClass.getSimpleName().toLowerCase(Locale.ROOT)
        );

        respond(ctx, 200, message, Map.of("data", list));
    }

    // ________________________________________________________

    @Override
    public void getByID(Context ctx) {
        int id = TryCatchService.tryParseInt(ctx.pathParam("id"), messageService.buildMessage(Notifications.MUST_BE_INT, ctx.pathParam("id")));

        E entity = TryCatchService.tryEntity(
            getEntityById(id),
            messageService.buildMessage(
                Notifications.NOT_FOUND_ID,
                entityClass.getSimpleName().substring(0,1).toUpperCase() + entityClass.getSimpleName().substring(1).toLowerCase(),
                String.valueOf(id)
            )
        );

        D dto = mapper.toDTO(entity);

        String message = messageService.buildMessage(
            Notifications.GET_BY_ID,
            entityClass.getSimpleName().toLowerCase(Locale.ROOT),
            String.valueOf(id)
        );

        respond(ctx, 200, message, Map.of("data", dto));
    }

    // ________________________________________________________

    protected User getAuthenticatedUser(Context ctx) {
        UserDTO userDTO = TryCatchService.tryEntity(
                ctx.attribute("user"),
                Notifications.NOT_LOGGED_IN.getDisplayName()
        );

        return TryCatchService.tryEntity(
            userDAO.getById(userDTO.getId()),
            messageService.buildMessage(
                Notifications.USER_NOT_FOUND_ID,
                String.valueOf(userDTO.getId())
            )
        );
    }

    // ________________________________________________________

    //TODO: Figure out if i wanna make them generic? Would it make sense to change "id" to id param instead? Maybe...
    protected int getPathId(Context ctx) {
        return TryCatchService.tryParseInt(
            ctx.pathParam("id"),
            Notifications.MUST_BE_INT.getDisplayName()
        );
    }

    // ________________________________________________________

    protected User getUserByID(Context ctx) {
        int userID = TryCatchService.tryParseInt(ctx.pathParam("user_id"), Notifications.MUST_BE_INT.getDisplayName());

        return TryCatchService.tryEntity(userDAO.getById(userID), Notifications.USER_NOT_FOUND_ID.getDisplayName());
    }

    // ________________________________________________________

    protected String getPathName(Context ctx) {
        return TryCatchService.tryString(
            ctx.pathParam("name"),
            Notifications.ENTER_NAME.getDisplayName()
        );
    }

    // ________________________________________________________

    protected void respond(Context ctx, int status, String message, Object data) {
        if (data != null) {
            ctx.status(status).json(Map.of(
                    "message", message,
                    "data", data
            ));
        } else {
            ctx.status(status).json(Map.of(
                    "message", message
            ));
        }
    }

    // ________________________________________________________

    protected Response verifyOwnershipResponse(Context ctx) {

        User user = getAuthenticatedUser(ctx);

        int id = getPathId(ctx);

        Response response = TryCatchService.tryEntity(
            responseDAO.getById(id),
            messageService.buildMessage(
                Notifications.NOT_FOUND_ID,
                "Response",
                String.valueOf(id)
            )
        );

        if (user.getId() != response.getUser().getId()) {
            String message = messageService.buildMessage(
                    Notifications.NOT_OWNED,
                    "Response",
                    String.valueOf(id)
            );

            respond(ctx, 403, message, null);
            return null;
        }

        return response;
    }
}
