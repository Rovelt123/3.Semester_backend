package app.controllers;

import app.Main;
import app.daos.ShiftRequestDAO;
import app.dtos.UserDTO;
import app.entities.ShiftRequest;
import app.entities.User;
import app.enums.Notifications;
import app.services.MessageService;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShiftRequestController {

    private static final ShiftRequestDAO shiftRequestDAO = Main.setup.getShiftRequestDAO();

    public static void registerRoutes(Javalin app) {

        //GET
        app.get("/request", ShiftRequestController::getAll);
        app.get("/request/{id}", ShiftRequestController::getByID);
    }

    // ________________________________________________________

    private static void getAll(Context ctx) {

        List<ShiftRequest> requests = new ArrayList<>(shiftRequestDAO.getAll());

        String message = MessageService.buildMessage(
                Notifications.SHIFTREQUEST_GET_BY_ID,
                String.valueOf(requests.size())
        );
        MessageService.notify(message);

        ctx.status(201).json(requests);
    }

    // ________________________________________________________

    private static void getByID(Context ctx) {

        try {
            int id = Integer.parseInt(ctx.pathParam("id"));

            ShiftRequest request = shiftRequestDAO.getById(id);
            if (request == null) {
                ctx.status(404);
                return;
            }
            String message = MessageService.buildMessage(Notifications.SHIFTREQUEST_GET_BY_ID, String.valueOf(id));
            MessageService.notify(message);
            ctx.status(200).json(Map.of(
                    "Data", request,
                    "Message", message
            ));
        } catch (NumberFormatException e) {
            MessageService.sendError(
                    MessageService.buildMessage(Notifications.MUST_BE_INT, ctx.pathParam("id"))
            );
            ctx.status(400);
        }
    }

    // ________________________________________________________

}
