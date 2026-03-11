package app.controllers;

import app.Main;
import app.controllers.Generic.BaseController;
import app.daos.ResponseDAO;
import app.dtos.ResponseDTO;
import app.entities.Response;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;

public class ResponseController extends BaseController<Response, ResponseDTO> {

    private static final ResponseDAO responseDAO = Main.setup.getResponseDAO();

    public ResponseController(){
        super(Response.class, ResponseDTO::new);
    }

    public static void registerRoutes(Javalin app){

        ResponseController controller = new ResponseController();

        app.get("/responses", controller::getAll);
        app.get("/response/{id}", controller::getByID);
        app.get("/responses/user/{id}", controller::getByUser);
        app.get("/responses/request/{id}", controller::getByRequest);
    }

    private void getByUser(Context ctx){

        int id = Integer.parseInt(ctx.pathParam("id"));

        ctx.json(responseDAO.getByUserId(id));
    }

    private void getByRequest(Context ctx){

        int id = Integer.parseInt(ctx.pathParam("id"));

        ctx.json(responseDAO.getById(id));
    }

    @Override
    protected List<Response> getAllEntities() {
        return responseDAO.getAll();
    }

    @Override
    protected Response getEntityById(int id) {
        return responseDAO.getById(id);
    }
}