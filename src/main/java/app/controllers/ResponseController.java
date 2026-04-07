package app.controllers;

import app.controllers.Generic.BaseController;
import app.dtos.ResponseDTO;
import app.entities.Response;
import app.entities.Shift;
import app.entities.ShiftRequest;
import app.entities.User;
import app.enums.Notifications;
import app.enums.Role;
import app.enums.ShiftStatus;
import app.utils.ErrorHandler;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.Context;
import java.util.List;
import java.util.Map;
import static io.javalin.apibuilder.ApiBuilder.*;

public class ResponseController extends BaseController<Response, ResponseDTO> {

    public ResponseController(){
        super(Response.class, responseMapper);
    }

    //________________________________________________________

    public static EndpointGroup registerRoutes(){

        ResponseController controller = new ResponseController();
        return () -> {
            get("/responses", controller::getAll, Role.USER);
            get("/response/{id}", controller::getByID, Role.USER);
            get("/responses/user/{id}", controller::getByUser, Role.USER);
            get("/responses/request/{id}", controller::getByRequest, Role.USER);

            put("/response/{id}/accept", controller::accept, Role.USER);
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
                .map(responseMapper::toDTO)
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
            .map(responseMapper::toDTO)
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

    private void accept(Context ctx) {
        User user = getAuthenticatedUser(ctx);

        int requestId = getPathId(ctx);

        Response response = ErrorHandler.tryEntity(
                responseDAO.getByUserAndShiftRequestId(user.getId(), requestId),
                messageService.buildMessage(
                        Notifications.NOT_FOUND_ID,
                        "Response",
                        String.valueOf(requestId)
                )
        );

        ShiftRequest request = ErrorHandler.tryEntity(shiftRequestDAO.getById(response.getShiftRequest().getId()),
                messageService.buildMessage(
                        Notifications.NOT_FOUND_ID,
                        "Shift request",
                        String.valueOf(requestId)
                )
        );

        if (request.getStatus().equals(ShiftStatus.SOLVED)) {
            respond(ctx, 500, Notifications.ALREADY_TAKEN.getDisplayName(), null);
            return;
        }

        Shift shift = ErrorHandler.tryEntity(
            request.getShift(),
            messageService.buildMessage(
                Notifications.NOT_FOUND_ID,
                "Shift",
                String.valueOf(requestId)
            )
        );

        response.setStatus(ShiftStatus.ACCEPTED);
        request.setStatus(ShiftStatus.SOLVED);
        shift.setOwner(user);
        shiftRequestDAO.update(request);
        responseDAO.update(response);
        shiftDAO.update(shift);

        String message = messageService.buildMessage(Notifications.SHIFT_TAKEN, String.valueOf(shift.getId()), user.getUsername());

        respond(ctx, 200, message, Map.of("shift", shiftMapper.toDTO(shift)));
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

        respond(ctx, 200, message, Map.of("data", responseMapper.toDTO(response)));
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