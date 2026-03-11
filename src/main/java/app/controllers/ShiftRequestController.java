package app.controllers;

import app.Main;
import app.controllers.Generic.BaseController;
import app.daos.ShiftDAO;
import app.daos.ShiftRequestDAO;
import app.daos.UserDAO;
import app.dtos.ShiftDTO;
import app.dtos.ShiftRequestDTO;
import app.entities.Shift;
import app.entities.ShiftRequest;
import app.entities.User;
import app.enums.Notifications;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;
import java.util.Map;

public class ShiftRequestController extends BaseController<ShiftRequest, ShiftRequestDTO> {

    private static final ShiftRequestDAO requestDAO = Main.setup.getShiftRequestDAO();
    private static final ShiftDAO shiftDAO = Main.setup.getShiftDAO();

    public ShiftRequestController(){
        super(ShiftRequest.class, ShiftRequestDTO::new);
    }

    public static void registerRoutes(Javalin app){

        ShiftRequestController controller = new ShiftRequestController();

        app.post("/shift-request", controller::createRequest);
        app.delete("/shift-request/{id}", controller::deleteRequest);

        app.get("/shift-requests", controller::getAll);
        app.get("/shift-request/{id}", controller::getByID);

        app.post("/shift-request/{id}/take", controller::takeShift);
    }

    private void createRequest(Context ctx){

        User user = ctx.sessionAttribute("user");

        Map<String,String> body = ctx.bodyAsClass(Map.class);

        int shiftId = Integer.parseInt(body.get("shift_id"));

        Shift shift = shiftDAO.getById(shiftId);

        if(shift.getOwner().getId() != user.getId()){
            ctx.status(403).json(Notifications.NOT_ALLOWED.getDisplayName());
            return;
        }

        ShiftRequest request = new ShiftRequest(user, shift);

        requestDAO.create(request);

        ctx.status(201).json(new ShiftRequestDTO(request));
    }

    private void deleteRequest(Context ctx){

        User user = ctx.sessionAttribute("user");

        int id = Integer.parseInt(ctx.pathParam("id"));

        ShiftRequest request = requestDAO.getById(id);

        if(request.getRequester().getId() != user.getId()){
            ctx.status(403).json(Notifications.NOT_ALLOWED.getDisplayName());
            return;
        }

        requestDAO.delete(request);

        ctx.json(Notifications.SHIFT_REQUEST_DELETED.getDisplayName());
    }

    private void takeShift(Context ctx){

        User user = ctx.sessionAttribute("user");

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

    @Override
    protected List<ShiftRequest> getAllEntities() {
        return requestDAO.getAll();
    }

    @Override
    protected ShiftRequest getEntityById(int id) {
        return requestDAO.getById(id);
    }
}