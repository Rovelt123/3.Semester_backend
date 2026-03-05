package app.Server;

import app.controllers.ShiftController;
import io.javalin.Javalin;

public class Routing {

    public static void registerRoutes(Javalin app) {
        ShiftController.registerRoutes(app);

        app.get("/", ctx -> ctx.result("Hello TeamPlanner!"));
    }
}
