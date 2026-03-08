package app.controllers;

import app.Main;
import app.controllers.Generic.BaseController;
import app.daos.ResponseDAO;
import app.dtos.ResponseDTO;
import app.entities.Response;
import io.javalin.Javalin;

import java.util.List;

public class ResponseController  extends BaseController<Response, ResponseDTO> {

    private static final ResponseDAO responseDAO = Main.setup.getResponseDAO();

    // ________________________________________________________

    public ResponseController() {
        super(Response.class, ResponseDTO::new);
    }

    // ________________________________________________________

    public static void registerRoutes(Javalin app) {
        ResponseController controller = new ResponseController();

        //GET
        app.get("/responses", controller::getAll);
        app.get("/response/{id}", controller::getByID);
    }

    // ________________________________________________________

    @Override
    protected List<Response> getAllEntities() {
        return responseDAO.getAll();
    }

    // ________________________________________________________

    @Override
    protected Response getEntityById(int id) {
        return responseDAO.getById(id);
    }

    // ________________________________________________________
}
