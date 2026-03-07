package app.controllers;

import app.Main;
import app.daos.HolidayDAO;
import io.javalin.Javalin;

public class HolidayController {

    private static final HolidayDAO holidayDAO = Main.setup.getHolidayDAO();

    public static void registerRoutes(Javalin app) {

    }
}
