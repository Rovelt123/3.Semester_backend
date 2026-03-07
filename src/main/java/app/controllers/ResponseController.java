package app.controllers;

import app.Main;
import app.daos.ResponseDAO;
import io.javalin.Javalin;

public class ResponseController {

    private static final ResponseDAO responseDAO = Main.setup.getResponseDAO();

    public static void registerRoutes(Javalin app) {

    }
}
