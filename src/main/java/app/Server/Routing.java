package app.Server;

import app.controllers.*;
import app.enums.Role;
import app.services.VersionControlService;
import io.javalin.Javalin;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.config.JavalinConfig;

import static io.javalin.apibuilder.ApiBuilder.*;
import static io.javalin.apibuilder.ApiBuilder.get;

public class Routing {

    public static EndpointGroup registerRoutes() {
        return () -> {
            UserController.registerRoutes().addEndpoints();
            ShiftController.registerRoutes().addEndpoints();
            ShiftRequestController.registerRoutes().addEndpoints();
            ResponsibilityController.registerRoutes().addEndpoints();
            ResponseController.registerRoutes().addEndpoints();
            HolidayController.registerRoutes().addEndpoints();
            AnnouncementController.registerRoutes().addEndpoints();

            get("/health", ctx -> ctx.result(VersionControlService.checkVersion()));
        };
    }
}
