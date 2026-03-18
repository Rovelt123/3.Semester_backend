package app.controllers;

import app.Main;
import app.controllers.Generic.BaseController;
import app.daos.ResponsibilityDAO;
import app.dtos.ResponsibilityDTO;
import app.entities.Responsibility;
import app.enums.Notifications;
import app.enums.Role;
import app.services.MessageService;
import app.services.TryCatchService;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.Context;

import java.util.List;

import static io.javalin.apibuilder.ApiBuilder.*;

public class ResponsibilityController extends BaseController<Responsibility, ResponsibilityDTO> {

    private static final ResponsibilityDAO responsibilityDAO = Main.setup.getRespDAO();

    //________________________________________________________

    public ResponsibilityController() {
        super(Responsibility.class, ResponsibilityDTO::new);
    }

    //________________________________________________________

    public static EndpointGroup registerRoutes(){

        ResponsibilityController controller = new ResponsibilityController();

        return () -> {
            post("/responsibility/{name}", controller::createResponsibility, Role.CHEF);
            put("/responsibility/{old}/update/{new}", controller::updateResponsibility, Role.CHEF);
            delete("/responsibility/{name}", controller::deleteResponsibility, Role.CHEF);

            get("/responsibilities", controller::getAll, Role.USER);
            get("/responsibility/{id}", controller::getByID, Role.USER);
            get("/responsibility/name/{name}", controller::getByName, Role.USER);
        };
    }

    //________________________________________________________

    private void createResponsibility(Context ctx){
        String name = getPathName(ctx);

        Responsibility r = new Responsibility(name);

        responsibilityDAO.create(r);

        ctx.status(201).json(r);
    }

    //________________________________________________________

    private void updateResponsibility(Context ctx){
        String oldResponsibility = TryCatchService.tryString(
                ctx.pathParam("old"),
                Notifications.ENTER_NAME.getDisplayName()
        );

        String newResponsibility = TryCatchService.tryString(
                ctx.pathParam("new"),
                Notifications.ENTER_NAME.getDisplayName()
        );

        Responsibility responsibility = TryCatchService.tryEntity(
                responsibilityDAO.getByName(oldResponsibility),
                MessageService.buildMessage(
                    Notifications.NOT_FOUND_WITH_NAME,
                    "Responsibility",
                    oldResponsibility
                )
        );

        responsibility.setName(newResponsibility);

        responsibilityDAO.update(responsibility);

        ctx.status(200).json(responsibility);
    }

    //________________________________________________________

    private void deleteResponsibility(Context ctx){
        String name = getPathName(ctx);

        Responsibility responsibility = TryCatchService.tryEntity(
            responsibilityDAO.getByName(name),
            MessageService.buildMessage(
                Notifications.RESPONSIBILITY_NOT_FOUND,
                name
            )

        );

        responsibilityDAO.deleteById(responsibility.getId());

        String message = MessageService.buildMessage(
            Notifications.RESPONSIBILITY_DELETED,
            name
        );

        ctx.status(200).json(message);
    }

    //________________________________________________________

    private void getByName(Context ctx){

        String name = getPathName(ctx);

        Responsibility responsibility = TryCatchService.tryEntity(
            responsibilityDAO.getByName(name),
            MessageService.buildMessage(
                Notifications.RESPONSIBILITY_NOT_FOUND,
                name
            )
        );

        ctx.status(200).json(new ResponsibilityDTO(responsibility));
    }

    //________________________________________________________

    @Override
    protected List<Responsibility> getAllEntities() {
        return responsibilityDAO.getAll();
    }

    //________________________________________________________

    @Override
    protected Responsibility getEntityById(int id) {
        return responsibilityDAO.getById(id);
    }
}