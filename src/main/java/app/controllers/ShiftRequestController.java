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
import app.services.MessageService;
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
            post("/shift-request", controller::createRequest, Role.USER);
            delete("/shift-request/{id}", controller::deleteRequest, Role.USER);

            get("/shift-requests", controller::getAll, Role.USER);
            get("/shift-request/{id}", controller::getByID, Role.USER);

            post("/shift-request/{id}/take", controller::takeShift, Role.USER);
        };
    }

    // ________________________________________________________

    private void createRequest(Context ctx) {
        UserDTO userDTO = ctx.attribute("user");
        if (userDTO == null) {
            ctx.status(401).json(Notifications.NOT_LOGGED_IN.getDisplayName());
            return;
        }

        User owner = userDAO.getById(userDTO.getId());

        Map<String, Integer> body = ctx.bodyAsClass(Map.class);
        int shiftId = body.get("shift_id");

        Shift shift = shiftDAO.getById(shiftId);
        if (shift == null) {
            ctx.status(404).json("Shift not found");
            return;
        }

        if (shift.getOwner().getId() != owner.getId()) {
            ctx.status(400).json("You don't own this shift!");
            return;
        }

        try {
            ShiftRequest request = new ShiftRequest(owner, shift);

            userDAO.getAll().stream()
                    .filter(u -> u.getId() != owner.getId())
                    .forEach(u -> request.getResponses().add(new Response(u, request)));

            requestDAO.create(request);

            ctx.status(201).json(new ShiftRequestDTO(request));
        } catch (Exception e) {
            ctx.status(500).json("Failed to create shift request: " + e.getMessage());
        }
    }

    // ________________________________________________________

    private void deleteRequest(Context ctx){

        UserDTO userDTO = ctx.attribute("user");
        User user = userDAO.getById(userDTO.getId());

        int id = Integer.parseInt(ctx.pathParam("id"));

        ShiftRequest request = requestDAO.getById(id);

        if(request == null) {
            String message = MessageService.buildMessage(Notifications.OBJECT_WITH_ID_NOT_FOUND,
                ShiftRequest.class.getSimpleName(),
                String.valueOf(id)
            );
            ctx.status(403).json(message);
            return;
        }

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

        UserDTO userDTO = ctx.attribute("user");

        User user = userDAO.getById(userDTO.getId());

        if(user == null){
            ctx.status(401).json("Not logged in");
            return;
        }

        int requestId = Integer.parseInt(ctx.pathParam("id"));

        try{

            Shift shift = requestDAO.takeShift(requestId, user.getId());

            ctx.json(Map.of(
                    "message", "Shift taken successfully",
                    "shift", new ShiftDTO(shift)
            ));

        }catch(Exception e){

            ctx.status(400).json(e.getMessage());
        }
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
}