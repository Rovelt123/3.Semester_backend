package app.Server;

import app.daos.*;
import app.enums.Notifications;
import app.enums.Role;
import app.services.MessageService;
import app.services.ThreadService;
import app.services.security.AccessService;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import jakarta.persistence.EntityManager;
import lombok.Getter;

import static app.services.VersionControlService.checkVersion;

@Getter
public class Setup {

    private final EntityManager em;
    private final Javalin app;
    private final AccessService accessService;
    private final ThreadService threadService;
    private final MessageService messageService;

    // ________________________________________________________

    private UserDAO userDAO;
    private ResponsibilityDAO respDAO;
    private HolidayDAO holidayDAO;
    private AnnouncementDAO announcementDAO;
    private ShiftRequestDAO shiftRequestDAO;
    private ShiftDAO shiftDAO;
    private ResponseDAO responseDAO;

    // ________________________________________________________

    public Setup(EntityManager em, int port) {
        this.em = em;
        this.accessService = new AccessService();
        this.threadService = new ThreadService(1);
        this.messageService = new MessageService();

        this.app = Javalin.create(Setup::configuration)
        .beforeMatched(accessService::accessHandler)
        .start(port);
    }

    // ________________________________________________________

    public static void configuration(JavalinConfig config) {
        config.showJavalinBanner = false;
        config.bundledPlugins.enableRouteOverview("/routes", Role.ANYONE);
        //TODO: Add more configs maybe???
    }

    // ________________________________________________________

    public void initialize(){

        // Gives me error message
        app.exception(Exception.class, (e, ctx) -> {
            ctx.status(500).result(e.getMessage());
        });

        // DAOS
        userDAO = new UserDAO(em);
        respDAO = new ResponsibilityDAO(em);
        holidayDAO = new HolidayDAO(em);
        announcementDAO = new AnnouncementDAO(em);
        shiftRequestDAO = new ShiftRequestDAO(em);
        shiftDAO = new ShiftDAO(em);
        responseDAO = new ResponseDAO(em);


        app.unsafeConfig().router.apiBuilder(Routing.registerRoutes());
        TestData.generate();
        System.out.println(checkVersion());
    }

    // ________________________________________________________

    public void endSession() {
        this.messageService.notify(Notifications.APP_CLOSING.getDisplayName());
        em.close();
        if (app != null) app.stop();
        this.messageService.notify(Notifications.APP_CLOSED.getDisplayName());
    }
}
