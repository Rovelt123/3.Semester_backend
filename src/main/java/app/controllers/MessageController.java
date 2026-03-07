package app.controllers;

import app.Main;
import app.daos.MessageDAO;
import io.javalin.Javalin;

public class MessageController {

    private static final MessageDAO messageDAO = Main.setup.getMessageDAO();

    public static void registerRoutes(Javalin app) {

    }
}
