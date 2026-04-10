package app.controllers;

import app.controllers.generic.BaseController;
import app.dtos.ShiftRequestDTO;
import app.entities.Response;
import app.entities.Shift;
import app.entities.ShiftRequest;
import app.entities.User;
import app.enums.Notifications;
import app.enums.Role;
import app.enums.ShiftStatus;
import app.services.MessageService;
import app.services.ThreadService;
import app.utils.ErrorHandler;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.Context;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static io.javalin.apibuilder.ApiBuilder.*;

public class ShiftRequestController extends BaseController<ShiftRequest, ShiftRequestDTO> {

    public ShiftRequestController(){
        super(ShiftRequest.class, shiftRequestMapper);
    }

    // ________________________________________________________

    public static EndpointGroup registerRoutes(){

        ShiftRequestController controller = new ShiftRequestController();

        return () ->{
            post("/shift_request/{id}", controller::createRequest, Role.USER);
            delete("/shift_request/{id}", controller::deleteRequest, Role.USER);

            get("/shift_requests", controller::getAll, Role.USER);
            get("/shift_request/{id}", controller::getByID, Role.USER);

            put("/shift_request/{id}", controller::update);

            delete("/shift_requests/clean", controller::checkOutdatedShiftRequests, Role.CHEF);
        };
    }

    // ________________________________________________________

    private void createRequest(Context ctx) {

        User owner = getAuthenticatedUser(ctx);

        int shiftId = ErrorHandler.tryParseInt(
            ctx.pathParam("id"),
            Notifications.MUST_BE_INT.getDisplayName()
        );

        Shift shift = ErrorHandler.tryEntity(
            shiftDAO.getById(shiftId),
            Notifications.SHIFT_NOT_FOUND.getDisplayName()
        );

        if (shift.getOwner().getId() != owner.getId() && !owner.getRoles().contains(Role.CHEF)) {
            respond(ctx, 400, Notifications.SHIFT_NOT_OWNED.getDisplayName(), null);
            return;
        }

        ShiftRequest request = ErrorHandler.tryEntity(
                ShiftRequest.builder()
                .requester(shift.getOwner())
                .shift(shift)
                .build(),
                Notifications.SHIFT_REQUEST_CREATE_FAILED.getDisplayName()
        );

        userDAO.getAll().stream()
                .filter(u -> u.getId() != shift.getOwner().getId()).forEach(u -> request.getResponses()
                    .add(
                        ErrorHandler.tryEntity(
                            Response.builder()
                                .user(u)
                                .shiftRequest(request)
                                .build(),
                            messageService.buildMessage(Notifications.RESPONSE_CREATION_FAILED, u.getUsername())
                        )
                    )
                );

        shiftRequestDAO.create(request);

        String message = messageService.buildMessage(
            Notifications.CREATED,
            "Shift request"
        );

        respond(ctx, 201, message, Map.of("data", shiftRequestMapper.toDTO(request)));
    }

    // ________________________________________________________

    private void deleteRequest(Context ctx){
        User user = getAuthenticatedUser(ctx);

        int id = getPathId(ctx);

        ShiftRequest request =  ErrorHandler.tryEntity(
            shiftRequestDAO.getById(id),
            messageService.buildMessage(
                Notifications.OBJECT_WITH_ID_NOT_FOUND,
                ShiftRequest.class.getSimpleName(),
                String.valueOf(id)
            )
        );


        if(request.getRequester().getId() != user.getId() && !user.getRoles().contains(Role.CHEF)){
            respond(ctx, 403, Notifications.NOT_ALLOWED.getDisplayName(), null);
            return;
        }

        List<Response> responses = responseDAO.getByShiftRequestId(request.getId());

        responses.forEach(responseDAO::delete);


        String message = messageService.buildMessage(
            Notifications.SHIFT_REQUEST_DELETED,
            String.valueOf(request.getId())
        );

        shiftRequestDAO.delete(request);

        respond(ctx, 200, message, null);
    }

    // ________________________________________________________

    @Override
    protected List<ShiftRequest> getAllEntities() {
        return shiftRequestDAO.getAll();
    }

    // ________________________________________________________

    @Override
    protected ShiftRequest getEntityById(int id) {
        return shiftRequestDAO.getById(id);
    }

    // ________________________________________________________

    // Ensures that new users can take shifts older than their creation of user... #BUGFIX
    public void checkActiveShiftRequests(User user) {
        MessageService m = new MessageService();
        shiftRequestDAO.getAll().forEach(s -> {

            boolean alreadyExists = s.getResponses().stream()
                    .anyMatch(r -> r.getUser().getId() == user.getId());

            if (!alreadyExists) {

                Response response = ErrorHandler.tryEntity(
                        Response.builder()
                                .user(user)
                                .shiftRequest(s)
                                .build(),
                        m.buildMessage(
                                Notifications.RESPONSE_CREATION_FAILED,
                                user.getUsername()
                        )
                );

                responseDAO.create(response);

            }
        });
    }

    // ________________________________________________________

    private void update(Context ctx) {

        int id = getPathId(ctx);

        ShiftRequest request = ErrorHandler.tryEntity(
            shiftRequestDAO.getById(id),
            messageService.buildMessage(
                Notifications.NOT_FOUND_ID,
                "ShiftRequest",
                String.valueOf(id)
            )
        );

        Map<String,String> body = ErrorHandler.tryBodyMap(
            ctx,
            Notifications.BODY_EMPTY.getDisplayName()
        );

        if(body.containsKey("status")){
            ShiftStatus status = ErrorHandler.tryParseEnum(
                ShiftStatus.class,
                body.get("status"),
                Notifications.ENUM_NOT_FOUND.getDisplayName()
            );
            request.setStatus(status);
        }

        if(body.containsKey("owner")){
            int userId = ErrorHandler.tryParseInt(
                body.get("owner"),
                Notifications.MUST_BE_INT.getDisplayName()
            );

            User newOwner = ErrorHandler.tryEntity(
                userDAO.getById(userId),
                messageService.buildMessage(
                    Notifications.USER_NOT_FOUND_ID,
                    String.valueOf(userId)
                )
            );

            // Delete response for the new owner
            Response response = request.getResponses().get(newOwner.getId());
            responseDAO.delete(response);

            // Create a new response for the old owner
            Response newResponse = Response.builder()
                .shiftRequest(request)
                .user(request.getRequester())
                .build();
            responseDAO.create(newResponse);

            request.setRequester(newOwner);

        }

        if(body.containsKey("shift")){
            int shiftId = ErrorHandler.tryParseInt(
                body.get("shift"),
                Notifications.MUST_BE_INT.getDisplayName()
            );

            Shift shift = ErrorHandler.tryEntity(
                shiftDAO.getById(shiftId),
                messageService.buildMessage(
                    Notifications.SHIFT_NOT_FOUND,
                    String.valueOf(shiftId)
                )
            );

            request.setShift(shift);
        }

        shiftRequestDAO.update(request);

        String message = messageService.buildMessage(
            Notifications.UPDATED,
            "Shift request"
        );

        respond(ctx, 200, message, Map.of("data", shiftRequestMapper.toDTO(request)));
    }

    // ________________________________________________________

    public void checkOutdatedShiftRequests(Context ctx) {
        ThreadService threadService = new ThreadService(1);

        threadService.runAsync(ShiftRequestController::cleanOutdatedShiftRequests);

        respond(ctx, 200, Notifications.DELETING_OUTDATED_SHIFTREQUESTS.getDisplayName(), null);
    }

    // ________________________________________________________

    //Static so the system also can run it by itself without instanciating ShiftRequestController
    public static void cleanOutdatedShiftRequests() {
        List<ShiftRequest> requests = shiftRequestDAO.getAll();

        LocalDate now = LocalDate.now();

        // To optimize, maybe make a method getOutdatedShiftRequests() in DAO? Might be "heavy" load if there's a lot of shiftRequests, if company is big?
        // Could be done as an update in the future!
        requests.stream()
        .filter(r -> {
            LocalDate shiftDate = r.getShift().getDate();

            boolean olderThan30Days = shiftDate.plusDays(30).isBefore(now);
            boolean isPastShift = shiftDate.isBefore(now);

            return
                (r.getStatus() == ShiftStatus.SOLVED && olderThan30Days)
                ||
                (r.getStatus() == ShiftStatus.WAITING && isPastShift);
        })
        .forEach(shiftRequestDAO::delete);
    }
}