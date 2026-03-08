package app.controllers;

import app.Main;
import app.controllers.Generic.BaseController;
import app.daos.ShiftRequestDAO;
import app.dtos.ShiftRequestDTO;
import app.entities.ShiftRequest;
import io.javalin.Javalin;
import java.util.List;

public class ShiftRequestController extends BaseController<ShiftRequest, ShiftRequestDTO> {

    private static final ShiftRequestDAO shiftRequestDAO = Main.setup.getShiftRequestDAO();

    // ________________________________________________________

    public ShiftRequestController() {
        super(ShiftRequest.class, ShiftRequestDTO::new);
    }

    // ________________________________________________________

    public static void registerRoutes(Javalin app) {
        ShiftRequestController controller = new ShiftRequestController();

        //GET
        app.get("/requests", controller::getAll);
        app.get("/request/{id}", controller::getByID);
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

}
