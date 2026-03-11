package app.controllers;

import app.Main;
import app.controllers.Generic.BaseController;
import app.daos.AnnouncementDAO;
import app.dtos.AnnouncementDTO;
import app.entities.Announcement;
import app.entities.User;
import app.enums.Role;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;
import java.util.Map;

public class AnnouncementController extends BaseController<Announcement, AnnouncementDTO> {

    private static final AnnouncementDAO announcementDAO = Main.setup.getAnnouncementDAO();

    public AnnouncementController(){
        super(Announcement.class, AnnouncementDTO::new);
    }

    public static void registerRoutes(Javalin app){

        AnnouncementController controller = new AnnouncementController();

        app.post("/announcement", controller::createAnnouncement);
        app.put("/announcement/{id}", controller::updateAnnouncement);
        app.delete("/announcement/{id}", controller::deleteAnnouncement);

        app.get("/announcements", controller::getAll);
        app.get("/announcement/{id}", controller::getByID);
    }

    private void createAnnouncement(Context ctx){

        User user = ctx.sessionAttribute("user");

        if(user.getRole() != Role.CHEF){
            ctx.status(403);
            return;
        }

        Map<String,String> body = ctx.bodyAsClass(Map.class);

        Announcement a = new Announcement(
                user,
                body.get("title"),
                body.get("content")
        );

        announcementDAO.create(a);

        ctx.status(201).json(new AnnouncementDTO(a));
    }

    private void updateAnnouncement(Context ctx){

        User user = ctx.sessionAttribute("user");

        if(user.getRole() != Role.CHEF){
            ctx.status(403);
            return;
        }

        int id = Integer.parseInt(ctx.pathParam("id"));

        Announcement a = announcementDAO.getById(id);

        Map<String,String> body = ctx.bodyAsClass(Map.class);

        a.updateContent(body.get("content"));

        announcementDAO.update(a);

        ctx.json(new AnnouncementDTO(a));
    }

    private void deleteAnnouncement(Context ctx){

        User user = ctx.sessionAttribute("user");

        if(user.getRole() != Role.CHEF){
            ctx.status(403);
            return;
        }

        int id = Integer.parseInt(ctx.pathParam("id"));

        announcementDAO.deleteById(id);

        ctx.json("Announcement deleted");
    }

    @Override
    protected List<Announcement> getAllEntities() {
        return announcementDAO.getAll();
    }

    @Override
    protected Announcement getEntityById(int id) {
        return announcementDAO.getById(id);
    }
}