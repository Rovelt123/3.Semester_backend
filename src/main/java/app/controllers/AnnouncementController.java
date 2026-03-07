package app.controllers;

import app.Main;
import app.daos.AnnouncementDAO;
import io.javalin.Javalin;

public class AnnouncementController {

    private static final AnnouncementDAO announcementDAO = Main.setup.getAnnouncementDAO();

    public static void registerRoutes(Javalin app) {

    }
}
