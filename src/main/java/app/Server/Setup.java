package app.Server;

import app.daos.*;
import io.javalin.Javalin;
import jakarta.persistence.EntityManager;
import lombok.Getter;

@Getter
public class Setup {

    private final EntityManager em;
    private final Javalin app;

    // ________________________________________________________

    private UserDAO userDAO;
    private ResponsibilityDAO respDAO;
    private HolidayDAO holidayDAO;
    private MessageDAO messageDAO;
    private AnnouncementDAO announcementDAO;
    private ShiftRequestDAO shiftRequestDAO;
    private ShiftDAO shiftDAO;

    // ________________________________________________________

    public Setup(EntityManager em, int port) {
        this.em = em;
        this.app = Javalin.create().start(port);
    }

    // ________________________________________________________

    public void initialize(){

        // Gives me error message
        app.exception(Exception.class, (e, ctx) -> {
            e.printStackTrace();
            ctx.status(500).result(e.getMessage());
        });

        userDAO = new UserDAO(em);
        respDAO = new ResponsibilityDAO(em);
        holidayDAO = new HolidayDAO(em);
        messageDAO = new MessageDAO(em);
        announcementDAO = new AnnouncementDAO(em);
        shiftRequestDAO = new ShiftRequestDAO(em);
        shiftDAO = new ShiftDAO(em);
        TestData.generate();
        Routing.registerRoutes(app);
    }



    // ________________________________________________________

    public void endSession() {
        em.close();
        if (app != null) app.stop();
    }
}
