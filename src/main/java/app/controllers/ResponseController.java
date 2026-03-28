package app.controllers;

import app.Main;
import app.controllers.Generic.BaseController;
import app.daos.ResponseDAO;
import app.dtos.ResponseDTO;
import app.entities.Response;
import app.entities.ShiftRequest;
import app.enums.Notifications;
import app.enums.Role;
import app.enums.ShiftStatus;
import app.services.MessageService;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.Context;

import java.util.List;
import java.util.Map;

import static io.javalin.apibuilder.ApiBuilder.*;

public class ResponseController extends BaseController<Response, ResponseDTO> {

    private static final ResponseDAO responseDAO = Main.setup.getResponseDAO();
    private final MessageService messageService = Main.setup.getMessageService();

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
                .toList();

        if (responses.isEmpty()) {
            String message = messageService.buildMessage(
                    Notifications.GET_ALL_EMPTY,
                    "Response with ID: " + id
            );
            respond(ctx, 200, message, null);
            return;
        }

        String message = messageService.buildMessage(
            Notifications.GET_BY_USER,
            "responses",
            String.valueOf(id)
        );

        respond(ctx, 200, message, Map.of("data", responses));
    }

    //________________________________________________________

    private void getByRequest(Context ctx){

        int id = getPathId(ctx);

        List<ResponseDTO> responses = responseDAO.getByShiftRequestId(id)
            .stream()
            .map(ResponseDTO::new)
            .toList();

        if (responses.isEmpty()) {
            String message = messageService.buildMessage(
                Notifications.GET_ALL_EMPTY,
                "Response with ID: " + id
            );
            respond(ctx, 200, message, null);
            return;
        }

        String message = messageService.buildMessage(
            Notifications.GET_BY_ID,
            "response",
            String.valueOf(id)
        );

        respond(ctx, 200, message, Map.of("data", responses));
    }

    //________________________________________________________

    private void reject(Context ctx){

        Response response = verifyOwnershipResponse(ctx);

        if (response == null) {
            return;
        }

        response.setStatus(ShiftStatus.REJECTED);
        responseDAO.update(response);

        String message = messageService.buildMessage(
            Notifications.RESPONSE_REJECTED,
            String.valueOf(response.getId())
        );

        respond(ctx, 200, message, Map.of("message", message));
    }

    //________________________________________________________

    private void noResponse(Context ctx){

        Response response = verifyOwnershipResponse(ctx);

        if (response == null) {
            return;
        }

        response.setStatus(ShiftStatus.NO_RESPONSE);
        responseDAO.update(response);

        String message = messageService.buildMessage(
            Notifications.CANCEL_RESPONSE,
            String.valueOf(response.getId())
        );

        respond(ctx, 200, message, Map.of("data", new ResponseDTO(response)));
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

    //________________________________________________________

    public static void deleteOutdatedResponses(ShiftRequest shiftRequest) {
        List<Response> responses = responseDAO.getAll();

        responses.stream()
            .filter(response -> response.getShiftRequest().getId() == shiftRequest.getId())
            .forEach(responseDAO::delete);
    }



}