package app.controllers.Generic;

import app.Main;
import app.daos.ResponseDAO;
import app.daos.UserDAO;
import app.dtos.UserDTO;
import app.entities.Response;
import app.entities.User;
import app.enums.Notifications;
import app.services.MessageService;
import app.services.TryCatchService;
import io.javalin.http.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;



public abstract class BaseController<T, DTO> implements IController {

    protected Class<T> entityClass;
    protected EntityMapper<T, DTO> mapper;
    protected abstract List<T> getAllEntities();
    protected abstract T getEntityById(int id);
    private final UserDAO userDAO = Main.setup.getUserDAO();
    private final ResponseDAO responseDAO = Main.setup.getResponseDAO();
    private final MessageService messageService = Main.setup.getMessageService();

    // ________________________________________________________

    protected BaseController(Class<T> entityClass, EntityMapper<T, DTO> mapper) {
        this.entityClass = entityClass;
        this.mapper = mapper;
    }

    // ________________________________________________________

    @Override
    public void getAll(Context ctx) {

        List<DTO> list = new ArrayList<>();

        if (getAllEntities().isEmpty()) {
            String message = messageService.buildMessage(Notifications.GET_ALL_EMPTY, entityClass.getSimpleName().toLowerCase(Locale.ROOT));
            respond(ctx, 200, message, null);
            return;
        }

        getAllEntities().forEach(entity -> list.add(mapper.map(entity)));


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

        T entity = TryCatchService.tryEntity(
            getEntityById(id),
            messageService.buildMessage(
                Notifications.NOT_FOUND_ID,
                entityClass.getSimpleName().substring(0,1).toUpperCase() + entityClass.getSimpleName().substring(1).toLowerCase(),
                String.valueOf(id)
            )
        );

        DTO dto = mapper.map(entity);

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
