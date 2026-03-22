package app.controllers;

import app.Main;
import app.controllers.Generic.BaseController;
import app.daos.ResponseDAO;
import app.dtos.ResponseDTO;
import app.dtos.UserDTO;
import app.entities.Message;
import app.entities.Response;
import app.entities.User;
import app.enums.Notifications;
import app.enums.Role;
import app.enums.ShiftStatus;
import app.services.MessageService;
import app.services.TryCatchService;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
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

            put("/response/{id}/accept", controller::transferShift, Role.USER);
            put("/response/{id}/reject", controller::reject, Role.USER);
            put("/response/{id}/no-response", controller::noResponse, Role.USER);
            put("/response/{id}", controller::updateResponse, Role.CHEF);
            delete("/response/{id}", controller::deleteResponse, Role.CHEF);
        };
    }

    //________________________________________________________

    private void getByUser(Context ctx){
        int id = getPathId(ctx);

        List<ResponseDTO> responses = responseDAO.getByColumn(id, "user.id")
                .stream()
                .map(ResponseDTO::new)
                .collect(Collectors.toList());

        if (responses.isEmpty()) {
            String Message = MessageService.buildMessage(
                    Notifications.GET_ALL_EMPTY,
                    "Response with ID: " + id
            );
            ctx.status(200).json(Map.of("message", Message));
            return;
        }

        ctx.status(200).json(responses);
    }

    //________________________________________________________

    private void getByRequest(Context ctx){

        int id = getPathId(ctx);

        List<ResponseDTO> responses = responseDAO.getByShiftRequestId(id)
                .stream()
                .map(ResponseDTO::new)
                .collect(Collectors.toList());

        if (responses.isEmpty()) {
            String Message = MessageService.buildMessage(
                    Notifications.GET_ALL_EMPTY,
                    "Response with ID: " + id
            );
            ctx.status(200).json(Map.of("message", Message));
            return;
        }

        String message = MessageService.buildMessage(
            Notifications.GET_BY_ID,
            "response",
            String.valueOf(id)
        );

        ctx.status(200).json(Map.of(
            "message", message,
            "data", responses
        ));
    }

    //________________________________________________________

    private void reject(Context ctx){

        User user = getAuthenticatedUser(ctx);

        int id = getPathId(ctx);

        Response response = TryCatchService.tryEntity(
                responseDAO.getById(id),
                MessageService.buildMessage(
                        Notifications.NOT_FOUND_ID,
                        "Response",
                        String.valueOf(id)
                )
        );

        if (user.getId() != response.getUser().getId()) {
            ctx.status(403).json(Map.of(
                "message", MessageService.buildMessage(
                    Notifications.NOT_OWNED,
                    "Response",
                    String.valueOf(id)
                )
            ));
            return;
        }

        response.setStatus(ShiftStatus.REJECTED);
        responseDAO.update(response);

        String message = MessageService.buildMessage(
            Notifications.RESPONSE_REJECTED,
            String.valueOf(id)
        );

        ctx.status(200).json(Map.of(
            "data", new ResponseDTO(response),
            "message", message
        ));
    }

    //________________________________________________________

    private void noResponse(Context ctx){

        User user = getAuthenticatedUser(ctx);

        int id = getPathId(ctx);

        Response response = TryCatchService.tryEntity(
            responseDAO.getById(id),
            MessageService.buildMessage(
                Notifications.NOT_FOUND_ID,
                "Response",
                String.valueOf(id)
            )
        );

        if (user.getId() != response.getUser().getId()) {
            ctx.status(403).json(
                MessageService.buildMessage(
                    Notifications.NOT_OWNED,
                    "Response",
                    String.valueOf(id)
                )
            );
            return;
        }

        response.setStatus(ShiftStatus.NO_RESPONSE);
        responseDAO.update(response);

        String message = MessageService.buildMessage(
            Notifications.CANCEL_RESPONSE,
            String.valueOf(id)
        );

        ctx.status(200).json(Map.of(
            "data", new ResponseDTO(response),
            "message", message
        ));
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

    //________________________________________________________

    private void deleteResponse (Context ctx) {
        System.out.println("Not made yet!");
    }

    //________________________________________________________

    private void updateResponse(Context ctx) {
        System.out.println("Not made yet!");
    }



}