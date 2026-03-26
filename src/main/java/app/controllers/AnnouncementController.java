package app.controllers;

import app.Main;
import app.controllers.Generic.BaseController;
import app.daos.AnnouncementDAO;
import app.dtos.AnnouncementDTO;
import app.entities.Announcement;
import app.entities.User;
import app.enums.Notifications;
import app.enums.Role;
import app.services.TryCatchService;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.Context;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static io.javalin.apibuilder.ApiBuilder.*;

public class AnnouncementController extends BaseController<Announcement, AnnouncementDTO> {

    private static final AnnouncementDAO announcementDAO = Main.setup.getAnnouncementDAO();

    //________________________________________________________

    public AnnouncementController(){
        super(Announcement.class, AnnouncementDTO::new);
    }

    //________________________________________________________

    public static EndpointGroup registerRoutes(){

        AnnouncementController controller = new AnnouncementController();

        return () -> {
            post("/announcement", controller::createAnnouncement, Role.CHEF);
            put("/announcement/{id}", controller::updateAnnouncement, Role.CHEF);
            delete("/announcement/{id}", controller::deleteAnnouncement, Role.CHEF);

            get("/announcements", controller::getAll, Role.USER);
            get("/announcement/{id}", controller::getByID, Role.USER);
        };

    }

    //________________________________________________________

    private void createAnnouncement(Context ctx){

        User user = getAuthenticatedUser(ctx);

        Map<String,String> body = TryCatchService.tryBodyMap(
            ctx,
            Notifications.BODY_EMPTY.getDisplayName()
        );

        Announcement a = Announcement.builder()
            .author(user)
            .title(body.get("title"))
            .content(body.get("content"))
            .lastUpdated(LocalDateTime.now())
            .build();

        announcementDAO.create(a);

        respond(ctx, 200, Notifications.ANNOUNCEMENT_CREATED.getDisplayName(), Map.of("data", new AnnouncementDTO(a)));
    }

    //________________________________________________________

    private void updateAnnouncement(Context ctx){

        int id = getPathId(ctx);

        Announcement a = announcementDAO.getById(id);

        Map<String,String> body = TryCatchService.tryBodyMap(
            ctx,
            Notifications.BODY_EMPTY.getDisplayName()
        );

        a.updateContent(body.get("content"));

        announcementDAO.update(a);

        respond(ctx, 200, Notifications.ANNOUNCEMENT_UPDATED.getDisplayName(), Map.of("data", new AnnouncementDTO(a)));
    }

    //________________________________________________________

    private void deleteAnnouncement(Context ctx){

        int id = getPathId(ctx);

        announcementDAO.deleteById(id);

        respond(ctx, 200, Notifications.ANNOUNCEMENT_DELETED.getDisplayName(), null);
    }

    //________________________________________________________

    @Override
    protected List<Announcement> getAllEntities() {
        return announcementDAO.getAll();
    }

    //________________________________________________________

    @Override
    protected Announcement getEntityById(int id) {
        return announcementDAO.getById(id);
    }
}