package app.Server;

import app.controllers.*;
import io.javalin.Javalin;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.config.JavalinConfig;

import static io.javalin.apibuilder.ApiBuilder.path;

public class Routing {

    public static EndpointGroup registerRoutes() {
        /*AnnouncementController.registerRoutes(app);
        HolidayController.registerRoutes(app);
        MessageController.registerRoutes(app);
        ResponseController.registerRoutes(app);
        ResponsibilityController.registerRoutes(app);
        ShiftController.registerRoutes(app);
        ShiftRequestController.registerRoutes(app);*/
        return () -> {
            path("/users", UserController.registerRoutes());
        };

    }
}
