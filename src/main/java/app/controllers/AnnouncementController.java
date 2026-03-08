package app.controllers;

import app.Main;
import app.controllers.Generic.BaseController;
import app.daos.AnnouncementDAO;
import app.dtos.AnnouncementDTO;
import app.entities.Announcement;
import io.javalin.Javalin;

import java.util.List;

public class AnnouncementController extends BaseController<Announcement, AnnouncementDTO> {

    private static final AnnouncementDAO announcementDAO = Main.setup.getAnnouncementDAO();

    // ________________________________________________________

    public AnnouncementController() {
        super(Announcement.class, AnnouncementDTO::new);
    }

    // ________________________________________________________

    public static void registerRoutes(Javalin app) {

        AnnouncementController controller = new AnnouncementController();

        app.get("/announcements", controller::getAll);
        app.get("/announcement/{id}", controller::getByID);
    }

    // ________________________________________________________

    @Override
    protected List<Announcement> getAllEntities() {
        return announcementDAO.getAll();
    }

    // ________________________________________________________

    @Override
    protected Announcement getEntityById(int id) {
        return announcementDAO.getById(id);
    }

    // ________________________________________________________
}
