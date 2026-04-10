package app.controllers;

import app.controllers.generic.BaseController;
import app.dtos.AnnouncementDTO;
import app.entities.Announcement;
import app.entities.User;
import app.enums.Notifications;
import app.enums.Role;
import app.utils.ErrorHandler;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.Context;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import static io.javalin.apibuilder.ApiBuilder.*;

public class AnnouncementController extends BaseController<Announcement, AnnouncementDTO> {


    public AnnouncementController(){
        super(Announcement.class, announcementMapper);
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
            get("/announcement/author/{author}", controller::getAnnouncementByAuthor, Role.USER);
        };

    }

    //________________________________________________________

    private void createAnnouncement(Context ctx){

        User user = getAuthenticatedUser(ctx);

        Map<String,String> body = ErrorHandler.tryBodyMap(
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

        respond(ctx, 200, Notifications.ANNOUNCEMENT_CREATED.getDisplayName(), Map.of(
                "data", announcementMapper.toDTO(a)
        ));
    }

    //________________________________________________________

    private void updateAnnouncement(Context ctx) {

        int id = getPathId(ctx);

        Announcement announcement = announcementDAO.getById(id);

        Map<String, String> body = ErrorHandler.tryBodyMap(
            ctx,
            Notifications.BODY_EMPTY.getDisplayName()
        );

        //ALWAYS UPDATE LAST UPDATED TO NOW (BUT CAN BE CHANGED ALSO!)
        announcement.setLastUpdated(LocalDateTime.now());

        if (body.containsKey("content")) {
            String content = ErrorHandler.tryString(
                body.get("content"),
                Notifications.MUST_ENTER_CONTENT.getDisplayName()
            );

            announcement.setContent(content);
        }

        if (body.containsKey("title")) {
            String title = ErrorHandler.tryString(
                body.get("title"),
                Notifications.MUST_ENTER_TITLE.getDisplayName()
            );
            announcement.setTitle(title);
        }

        if (body.containsKey("owner")) {
            User user = ErrorHandler.tryEntity(
                userDAO.getById(body.get("owner")),
                messageService.buildMessage(
                    Notifications.USER_NOT_FOUND_ID,
                    body.get("owner")
                )
            );
            announcement.setAuthor(user);
        }

        //OVERRIDES LAST UPDATED, IF YOU WANT TO OVERRIDE IT WITH A SPECIFIC DATE
        if (body.containsKey("lastupdated")) {
            LocalDateTime date = ErrorHandler.tryParseLocalDateTime(
                body.get("lastupdated"),
                messageService.buildMessage(
                    Notifications.MUST_BE_DATETIME_FORMAT,
                    body.get("lastupdated")
                )
            );
            announcement.setLastUpdated(date);
        }

        announcementDAO.update(announcement);

        respond(ctx, 200, Notifications.ANNOUNCEMENT_UPDATED.getDisplayName(), Map.of(
                "data", announcementMapper.toDTO(announcement)
        ));
    }

    //________________________________________________________

    private void deleteAnnouncement(Context ctx){

        int id = getPathId(ctx);

        announcementDAO.deleteById(id);

        respond(ctx, 200, Notifications.ANNOUNCEMENT_DELETED.getDisplayName(), null);
    }

    //________________________________________________________

    private void getAnnouncementByAuthor(Context ctx) {

        String author = ErrorHandler.tryString(
                ctx.pathParam("author"),
                Notifications.MUST_ENTER_USERID.getDisplayName()
        );

        List<AnnouncementDTO> announcements = ErrorHandler.tryList(
            announcementDAO.getByColumn(author, "author.id"),
                messageService.buildMessage(
                    Notifications.ANNOUNCEMENT_NOT_FOUND_BY_AUTHOR,
                    author
            )
        ).stream()
        .map(announcementMapper::toDTO)
        .toList();

        String message = "";
        if (announcements.size() == 1) {
            message = messageService.buildMessage(
                    Notifications.ANNOUNCEMENT_FOUND_WITH_AUTHOR,
                    author
            );
        } else if  (announcements.size() > 1) {
            message = messageService.buildMessage(
                    Notifications.ANNOUNCEMENTS_FOUND_WITH_AUTHOR,
                    String.valueOf(announcements.size()),
                    author
            );

        }

        respond(ctx, 200, message, Map.of("data", announcements));
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