package app.controllers;

import app.Main;
import app.controllers.Generic.BaseController;
import app.daos.ResponseDAO;
import app.daos.ShiftDAO;
import app.daos.ShiftRequestDAO;
import app.daos.UserDAO;
import app.dtos.ShiftRequestDTO;
import app.entities.Response;
import app.entities.Shift;
import app.entities.ShiftRequest;
import app.entities.User;
import app.enums.Notifications;
import app.enums.Role;
import app.enums.ShiftStatus;
import app.services.MessageService;
import app.services.TryCatchService;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.Context;

import java.util.List;
import java.util.Map;

import static io.javalin.apibuilder.ApiBuilder.*;

public class ShiftRequestController extends BaseController<ShiftRequest, ShiftRequestDTO> {

    private static final ShiftRequestDAO requestDAO = Main.setup.getShiftRequestDAO();
    private static final ShiftDAO shiftDAO = Main.setup.getShiftDAO();
    private static final UserDAO userDAO = Main.setup.getUserDAO();
    private static final ResponseDAO responseDAO = Main.setup.getResponseDAO();

    // ________________________________________________________

    public ShiftRequestController(){
        super(ShiftRequest.class, ShiftRequestDTO::new);
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
        };
    }

    // ________________________________________________________

    private void createRequest(Context ctx) {

        User owner = getAuthenticatedUser(ctx);

        int shiftId = TryCatchService.tryParseInt(
                ctx.pathParam("id"),
                Notifications.MUST_BE_INT.getDisplayName()
        );

        Shift shift = TryCatchService.tryEntity(
            shiftDAO.getById(shiftId),
            Notifications.SHIFT_NOT_FOUND.getDisplayName()
        );

        if (shift.getOwner().getId() != owner.getId() && !owner.getRoles().contains(Role.CHEF)) {
            respond(ctx, 400, Notifications.SHIFT_NOT_OWNED.getDisplayName(), null);
            return;
        }

        ShiftRequest request = TryCatchService.tryEntity(ShiftRequest.builder().requester(owner).shift(shift).build(), Notifications.SHIFT_REQUEST_CREATE_FAILED.getDisplayName());

        userDAO.getAll().stream()
                .filter(u -> u.getId() != owner.getId()).forEach(u -> request.getResponses()
                    .add(
                        TryCatchService.tryEntity(
                            Response.builder()
                                .user(u)
                                .shiftRequest(request)
                                .build(),
                            MessageService.buildMessage(Notifications.RESPONSE_CREATION_FAILED, u.getUsername())
                        )
                    )
                );

        requestDAO.create(request);

        String message = MessageService.buildMessage(
            Notifications.CREATED,
            "Shift request"
        );

        respond(ctx, 201, message, Map.of("data", new ShiftRequestDTO(request)));
    }

    // ________________________________________________________

    private void deleteRequest(Context ctx){
        User user = getAuthenticatedUser(ctx);

        int id = getPathId(ctx);

        ShiftRequest request =  TryCatchService.tryEntity(
            requestDAO.getById(id),
            MessageService.buildMessage(
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


        String message = MessageService.buildMessage(
            Notifications.SHIFT_REQUEST_DELETED,
            String.valueOf(request.getId())
        );

        requestDAO.delete(request);

        respond(ctx, 200, message, null);
    }

    // ________________________________________________________

    @Override
    protected List<ShiftRequest> getAllEntities() {
        return requestDAO.getAll();
    }

    // ________________________________________________________

    @Override
    protected ShiftRequest getEntityById(int id) {
        return requestDAO.getById(id);
    }

    // ________________________________________________________

    // Ensures that new users can take shifts older than their creation of user... #BUGFIX
    public static void checkActiveShiftRequests(User user) {
        requestDAO.getAll().forEach(s -> {

            boolean alreadyExists = s.getResponses().stream()
                    .anyMatch(r -> r.getUser().getId() == user.getId());

            if (!alreadyExists) {

                Response response = TryCatchService.tryEntity(
                        Response.builder()
                                .user(user)
                                .shiftRequest(s)
                                .build(),
                        MessageService.buildMessage(
                                Notifications.RESPONSE_CREATION_FAILED,
                                user.getUsername()
                        )
                );

                responseDAO.create(response);

            }
        });
    }

    // ________________________________________________________
    //TODO:
    // Set owner by admin (If I decide to do this, I MUST DELETE ACTIVE RESPONSES AND ADD TO THE OLD OWNER OF SHIFT REQUEST!) (Made in update???)
    // delete 30 days after shift ended? Just to make sure the database does not get spammed?

    private void update(Context ctx) {

        int id = getPathId(ctx);

        ShiftRequest request = TryCatchService.tryEntity(
            requestDAO.getById(id),
            MessageService.buildMessage(
                Notifications.NOT_FOUND_ID,
                "ShiftRequest",
                String.valueOf(id)
            )
        );

        Map<String,String> body = TryCatchService.tryBodyMap(
            ctx,
            Notifications.BODY_EMPTY.getDisplayName()
        );

        if(body.containsKey("status")){
            ShiftStatus status = TryCatchService.tryParseEnum(
                ShiftStatus.class,
                body.get("status"),
                Notifications.ENUM_NOT_FOUND.getDisplayName()
            );
            request.setStatus(status);
        }

        if(body.containsKey("owner")){
            int userId = TryCatchService.tryParseInt(
                body.get("owner"),
                Notifications.MUST_BE_INT.getDisplayName()
            );

            User newOwner = TryCatchService.tryEntity(
                userDAO.getById(userId),
                MessageService.buildMessage(
                    Notifications.USER_NOT_FOUND_ID,
                    String.valueOf(userId)
                )
            );

            request.setRequester(newOwner);
        }

        if(body.containsKey("shift")){
            int shiftId = TryCatchService.tryParseInt(
                body.get("shift"),
                Notifications.MUST_BE_INT.getDisplayName()
            );

            Shift shift = TryCatchService.tryEntity(
                shiftDAO.getById(shiftId),
                MessageService.buildMessage(
                    Notifications.SHIFT_NOT_FOUND,
                    String.valueOf(shiftId)
                )
            );

            request.setShift(shift);
        }

        requestDAO.update(request);

        String message = MessageService.buildMessage(
            Notifications.UPDATED,
            "Shift request"
        );

        respond(ctx, 200, message, Map.of("data", new ShiftRequestDTO(request)));
    }

    // ________________________________________________________

    public void checkOutdatedShiftRequests() {
        System.out.println("NOT MADE YET!");
        //TODO:
        // If outdated, also delete all responses!
    }
}