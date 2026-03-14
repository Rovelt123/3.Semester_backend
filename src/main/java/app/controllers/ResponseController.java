package app.controllers;

import app.Main;
import app.controllers.Generic.BaseController;
import app.daos.ResponseDAO;
import app.dtos.ResponseDTO;
import app.entities.Response;
import app.enums.Role;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.Context;

import java.util.List;
import java.util.stream.Collectors;

import static io.javalin.apibuilder.ApiBuilder.*;

public class ResponseController extends BaseController<Response, ResponseDTO> {

    private static final ResponseDAO responseDAO = Main.setup.getResponseDAO();

    //________________________________________________________

    public ResponseController(){
        super(Response.class, ResponseDTO::new);
    }

    //________________________________________________________

    public static EndpointGroup registerRoutes(){

        ResponseController controller = new ResponseController();
        return () -> {
            get("/responses", controller::getAll, Role.USER);
            get("/response/{id}", controller::getByID, Role.USER);
            get("/responses/user/{id}", controller::getByUser, Role.USER);
            get("/responses/request/{id}", controller::getByRequest, Role.USER);
        };
    }

    //________________________________________________________

    private void getByUser(Context ctx){
        int id = Integer.parseInt(ctx.pathParam("id"));

        Response response= responseDAO.getByUserId(id);

        ctx.json(new ResponseDTO(response));
    }

    //________________________________________________________

    private void getByRequest(Context ctx){

        int id = Integer.parseInt(ctx.pathParam("id"));

        List<ResponseDTO> responses = responseDAO.getByShiftRequestId(id)
                .stream()
                .map(ResponseDTO::new)
                .collect(Collectors.toList());

        ctx.json(responses);
    }

    //________________________________________________________

    @Override
    protected List<Response> getAllEntities() {
        return responseDAO.getAll();
    }

    //________________________________________________________

    @Override
    protected Response getEntityById(int id) {
        return responseDAO.getById(id);
    }
}