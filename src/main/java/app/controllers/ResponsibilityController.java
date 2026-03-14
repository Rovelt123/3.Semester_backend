package app.controllers;

import app.Main;
import app.controllers.Generic.BaseController;
import app.daos.ResponsibilityDAO;
import app.dtos.ResponsibilityDTO;
import app.entities.Responsibility;
import app.entities.ShiftRequest;
import app.entities.User;
import app.enums.Notifications;
import app.enums.Role;
import app.services.MessageService;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.Context;

import java.util.List;
import java.util.Map;

import static io.javalin.apibuilder.ApiBuilder.*;

public class ResponsibilityController extends BaseController<Responsibility, ResponsibilityDTO> {

    private static final ResponsibilityDAO responsibilityDAO = Main.setup.getRespDAO();

    public ResponsibilityController() {
        super(Responsibility.class, ResponsibilityDTO::new);
    }

    public static EndpointGroup registerRoutes(){

        ResponsibilityController controller = new ResponsibilityController();

        return () -> {
            post("/responsibility", controller::createResponsibility, Role.CHEF);
            put("/responsibility/{id}", controller::updateResponsibility, Role.CHEF);
            delete("/responsibility/{id}", controller::deleteResponsibility, Role.CHEF);

            get("/responsibilities", controller::getAll, Role.USER);
            get("/responsibility/{id}", controller::getByID, Role.USER);
            get("/responsibility/name/{name}", controller::getByName, Role.USER);
        };
    }

    private void createResponsibility(Context ctx){

        User user = ctx.attribute("user");

        if(user == null || user.getRoles().stream().anyMatch(role -> role.equals(Role.CHEF))){
            ctx.status(403).json(Notifications.ADMINS_ONLY.getDisplayName());
            return;
        }

        Map<String,String> body = ctx.bodyAsClass(Map.class);

        Responsibility r = new Responsibility(body.get("name"));

        responsibilityDAO.create(r);

        ctx.status(201).json(new ResponsibilityDTO(r));
    }

    private void updateResponsibility(Context ctx){

        User user = ctx.attribute("user");

        if(user == null || user.getRoles().stream().anyMatch(role -> role.equals(Role.CHEF))){
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

        User user = ctx.attribute("user");

        if(user == null || user.getRoles().stream().anyMatch(role -> role.equals(Role.CHEF))){
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
        if (r == null){
            String message = MessageService.buildMessage(Notifications.OBJECT_WITH_NAME_NOT_FOUND,
                Responsibility.class.getSimpleName(),
                name
            );
            ctx.status(403).json(message);
            return;
        }
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