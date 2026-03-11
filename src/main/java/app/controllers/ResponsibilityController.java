package app.controllers;

import app.Main;
import app.controllers.Generic.BaseController;
import app.daos.ResponsibilityDAO;
import app.dtos.ResponsibilityDTO;
import app.entities.Responsibility;
import app.entities.User;
import app.enums.Notifications;
import app.enums.Role;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;
import java.util.Map;

public class ResponsibilityController extends BaseController<Responsibility, ResponsibilityDTO> {

    private static final ResponsibilityDAO responsibilityDAO = Main.setup.getRespDAO();

    public ResponsibilityController() {
        super(Responsibility.class, ResponsibilityDTO::new);
    }

    public static void registerRoutes(Javalin app){

        ResponsibilityController controller = new ResponsibilityController();

        app.post("/responsibility", controller::createResponsibility);
        app.put("/responsibility/{id}", controller::updateResponsibility);
        app.delete("/responsibility/{id}", controller::deleteResponsibility);

        app.get("/responsibilities", controller::getAll);
        app.get("/responsibility/{id}", controller::getByID);
        app.get("/responsibility/name/{name}", controller::getByName);
    }

    private void createResponsibility(Context ctx){

        User user = ctx.sessionAttribute("user");

        if(user == null || user.getRole() != Role.CHEF){
            ctx.status(403).json(Notifications.ADMINS_ONLY.getDisplayName());
            return;
        }

        Map<String,String> body = ctx.bodyAsClass(Map.class);

        Responsibility r = new Responsibility(body.get("name"));

        responsibilityDAO.create(r);

        ctx.status(201).json(new ResponsibilityDTO(r));
    }

    private void updateResponsibility(Context ctx){

        User user = ctx.sessionAttribute("user");

        if(user == null || user.getRole() != Role.CHEF){
            ctx.status(403).json(Notifications.ADMINS_ONLY.getDisplayName());
            return;
        }

        int id = Integer.parseInt(ctx.pathParam("id"));

        Responsibility r = responsibilityDAO.getById(id);

        Map<String,String> body = ctx.bodyAsClass(Map.class);

        r.setName(body.get("name"));

        responsibilityDAO.update(r);

        ctx.json(new ResponsibilityDTO(r));
    }

    private void deleteResponsibility(Context ctx){

        User user = ctx.sessionAttribute("user");

        if(user == null || user.getRole() != Role.CHEF){
            ctx.status(403).json(Notifications.ADMINS_ONLY.getDisplayName());
            return;
        }

        int id = Integer.parseInt(ctx.pathParam("id"));

        responsibilityDAO.deleteById(id);

        ctx.json(Notifications.RESPONSIBILITY_DELETED.getDisplayName());
    }

    private void getByName(Context ctx){

        String name = ctx.pathParam("name");

        Responsibility r = responsibilityDAO.getByName(name);

        ctx.json(new ResponsibilityDTO(r));
    }

    @Override
    protected List<Responsibility> getAllEntities() {
        return responsibilityDAO.getAll();
    }

    @Override
    protected Responsibility getEntityById(int id) {
        return responsibilityDAO.getById(id);
    }
}