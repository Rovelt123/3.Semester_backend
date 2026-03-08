package app.Server;

import app.controllers.*;
import io.javalin.Javalin;

public class Routing {

    public static void registerRoutes(Javalin app) {
        AnnouncementController.registerRoutes(app);
        HolidayController.registerRoutes(app);
        MessageController.registerRoutes(app);
        ResponseController.registerRoutes(app);
        ResponsibilityController.registerRoutes(app);
        ShiftController.registerRoutes(app);
        ShiftRequestController.registerRoutes(app);
        UserController.registerRoutes(app);
    }
}
