package app.Server;

import app.controllers.*;
import io.javalin.Javalin;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.config.JavalinConfig;

import static io.javalin.apibuilder.ApiBuilder.*;
import static io.javalin.apibuilder.ApiBuilder.get;

public class Routing {

    public static EndpointGroup registerRoutes() {
        /*AnnouncementController.registerRoutes(app);
        HolidayController.registerRoutes(app);
        MessageController.registerRoutes(app);
        ResponseController.registerRoutes(app);
        1ResponsibilityController.registerRoutes(app);
        1ShiftController.registerRoutes(app);
        1ShiftRequestController.registerRoutes(app);*/
        return () -> {
            UserController.registerRoutes().addEndpoints();
            ShiftController.registerRoutes().addEndpoints();
            ShiftRequestController.registerRoutes().addEndpoints();
            ResponsibilityController.registerRoutes().addEndpoints();
            ResponseController.registerRoutes().addEndpoints();
            MessageController.registerRoutes().addEndpoints();
            HolidayController.registerRoutes().addEndpoints();
            AnnouncementController.registerRoutes().addEndpoints();
        };

    }
}
