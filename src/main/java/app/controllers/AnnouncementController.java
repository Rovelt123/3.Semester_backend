package app.controllers;

import app.Main;
import app.controllers.Generic.BaseController;
import app.daos.AnnouncementDAO;
import app.daos.UserDAO;
import app.dtos.AnnouncementDTO;
import app.dtos.UserDTO;
import app.entities.Announcement;
import app.entities.User;
import app.enums.Role;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.Context;

import java.util.List;
import java.util.Map;

import static io.javalin.apibuilder.ApiBuilder.*;

public class AnnouncementController extends BaseController<Announcement, AnnouncementDTO> {

    private static final AnnouncementDAO announcementDAO = Main.setup.getAnnouncementDAO();
    private static final UserDAO userDao = Main.setup.getUserDAO();

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

        UserDTO userDTO = ctx.attribute("user");
        User user = userDao.getById(userDTO.getId());

        if(user.getRoles().stream().anyMatch(role -> role.equals(Role.CHEF))){
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

    //________________________________________________________

    private void updateAnnouncement(Context ctx){

        UserDTO userDTO = ctx.attribute("user");
        User user = userDao.getById(userDTO.getId());

        if(user.getRoles().stream().anyMatch(role -> role.equals(Role.CHEF))){
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

    //________________________________________________________

    private void deleteAnnouncement(Context ctx){

        UserDTO userDTO = ctx.attribute("user");
        User user = userDao.getById(userDTO.getId());

        if(user.getRoles().stream().anyMatch(role -> role.equals(Role.CHEF))){
            ctx.status(403);
            return;
        }

        int id = Integer.parseInt(ctx.pathParam("id"));

        announcementDAO.deleteById(id);

        ctx.json("Announcement deleted");
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