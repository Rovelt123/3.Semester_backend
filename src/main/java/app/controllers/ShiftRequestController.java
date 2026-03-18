package app.controllers;

import app.Main;
import app.controllers.Generic.BaseController;
import app.daos.ResponseDAO;
import app.daos.ShiftDAO;
import app.daos.ShiftRequestDAO;
import app.daos.UserDAO;
import app.dtos.ShiftDTO;
import app.dtos.ShiftRequestDTO;
import app.dtos.UserDTO;
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
            post("/shiftrequest/{id}", controller::createRequest, Role.USER);
            delete("/shiftrequest/{id}", controller::deleteRequest, Role.USER);

            get("/shiftrequests", controller::getAll, Role.USER);
            get("/shiftrequest/{id}", controller::getByID, Role.USER);

            post("/shiftrequest/{id}/take", controller::takeShift, Role.USER);
        };
    }

    // ________________________________________________________

    private void createRequest(Context ctx) {
        UserDTO userDTO = TryCatchService.tryEntity(
                ctx.attribute("user"),
                Notifications.NOT_LOGGED_IN.getDisplayName()
        );


        User owner = TryCatchService.tryEntity(
                userDAO.getById(userDTO.getId()),
                Notifications.NOT_LOGGED_IN.getDisplayName()
        );


        int shiftId = TryCatchService.tryParseInt(
                ctx.pathParam("id"),
                Notifications.MUST_BE_INT.getDisplayName()
        );

        Shift shift = TryCatchService.tryEntity(
            shiftDAO.getById(shiftId),
            Notifications.SHIFT_NOT_FOUND.getDisplayName()
        );

        if (shift.getOwner().getId() != owner.getId()) {
            ctx.status(400).json(Notifications.SHIFT_NOT_OWNED.getDisplayName());
            return;
        }

        System.out.println("6");
        ShiftRequest request = TryCatchService.tryEntity(new ShiftRequest(owner, shift), Notifications.SHIFT_REQUEST_CREATE_FAILED.getDisplayName());

        System.out.println("7");
        userDAO.getAll().stream()
                .filter(u -> u.getId() != owner.getId()).forEach(u -> request.getResponses()
                    .add(
                        TryCatchService.tryEntity(
                            new Response(u, request),
                            MessageService.buildMessage(Notifications.RESPONSE_CREATION_FAILED, u.getUsername())
                        )
                    )
                );

        System.out.println("8");
        requestDAO.create(request);

        ctx.status(201).json(new ShiftRequestDTO(request));
    }

    // ________________________________________________________

    private void deleteRequest(Context ctx){

        UserDTO userDTO = TryCatchService.tryEntity(ctx.attribute("user"), Notifications.NOT_LOGGED_IN.getDisplayName());

        User user = TryCatchService.tryEntity(
                userDAO.getById(userDTO.getId()),
                MessageService.buildMessage(
                        Notifications.USER_NOT_FOUND_ID,
                        String.valueOf(userDTO.getId())
                )
        );

        int id = TryCatchService.tryParseInt(ctx.pathParam("id"),  Notifications.MUST_BE_INT.getDisplayName());

        ShiftRequest request =  TryCatchService.tryEntity(
            requestDAO.getById(id),
            MessageService.buildMessage(
                Notifications.OBJECT_WITH_ID_NOT_FOUND,
                ShiftRequest.class.getSimpleName(),
                String.valueOf(id)
            )
        );


        if(request.getRequester().getId() != user.getId()){
            ctx.status(403).json(Notifications.NOT_ALLOWED.getDisplayName());
            return;
        }

        String message = MessageService.buildMessage(
                Notifications.SHIFT_REQUEST_DELETED,
                String.valueOf(request.getId())
        );

        requestDAO.delete(request);

        ctx.json(message);
    }

    // ________________________________________________________

    private void takeShift(Context ctx){

        UserDTO userDTO = TryCatchService.tryEntity(
                ctx.attribute("user"),
                Notifications.NOT_LOGGED_IN.getDisplayName()
        );

        User user = TryCatchService.tryEntity(
            userDAO.getById(userDTO.getId()),
            MessageService.buildMessage(
                Notifications.USER_NOT_FOUND_ID,
                String.valueOf(userDTO.getId())
            )
        );

        int requestId = TryCatchService.tryParseInt(
                ctx.pathParam("id"),
                Notifications.MUST_BE_INT.getDisplayName()
        );

        ShiftRequest request = TryCatchService.tryEntity(requestDAO.getById(requestId),
            MessageService.buildMessage(
                Notifications.NOT_FOUND_ID,
                "Shift request",
                String.valueOf(requestId)
            )
        );

        Shift shift = TryCatchService.tryEntity(
                request.getShift(),
                MessageService.buildMessage(
                        Notifications.NOT_FOUND_ID,
                        "Shift",
                        String.valueOf(requestId)
                )
        );

        Response response = TryCatchService.tryEntity(
            responseDAO.getByUserId(user.getId()),
            MessageService.buildMessage(
                    Notifications.NOT_FOUND_ID,
                    "Response",
                    String.valueOf(requestId)
            )
        );

        response.setStatus(ShiftStatus.ACCEPTED);
        request.solve();
        shift.setOwner(user);
        requestDAO.update(request);
        responseDAO.update(response);
        shiftDAO.update(shift);

        MessageService.buildMessage(Notifications.SHIFT_TAKEN, String.valueOf(shift.getId()), user.getUsername());
        ctx.json(Map.of(
                "message", "Shift taken successfully",
                "shift", new ShiftDTO(shift)
        ));

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

    // Ensures that new users can take shifts older then their creation of user... #BUG
    public static void checkActiveShiftRequests(User user) {
        requestDAO.getAll().forEach(s -> {

            boolean alreadyExists = s.getResponses().stream()
                    .anyMatch(r -> r.getUser().getId() == user.getId());

            if (!alreadyExists) {

                Response response = TryCatchService.tryEntity(
                        new Response(user, s),
                        MessageService.buildMessage(
                                Notifications.RESPONSE_CREATION_FAILED,
                                user.getUsername()
                        )
                );

                responseDAO.create(response);

            }
        });
    }
    //TODO:
    // Update shiftRequest?
    // Delete shiftRequest by admin?
    // Create shiftRequests by admin for users??
}