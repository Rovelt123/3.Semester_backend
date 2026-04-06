package app.controllers;

import app.controllers.Generic.BaseController;
import app.dtos.ResponsibilityDTO;
import app.entities.Responsibility;
import app.enums.Notifications;
import app.enums.Role;
import app.utils.ErrorHandler;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.Context;
import java.util.List;
import java.util.Map;
import static io.javalin.apibuilder.ApiBuilder.*;

public class ResponsibilityController extends BaseController<Responsibility, ResponsibilityDTO> {

    //________________________________________________________

    public ResponsibilityController() {
        super(Responsibility.class, responsibilityMapper);
    }

    //________________________________________________________

    public static EndpointGroup registerRoutes(){

        ResponsibilityController controller = new ResponsibilityController();

        return () -> {
            post("/responsibility/{name}", controller::createResponsibility, Role.CHEF);
            put("/responsibility/{old}/update/{new}", controller::updateResponsibility, Role.CHEF);
            delete("/responsibility/{name}", controller::deleteResponsibility, Role.CHEF);

            get("/responsibilities", controller::getAll, Role.USER);
            get("/responsibility/id/{id}", controller::getByID, Role.USER);
            get("/responsibility/name/{name}", controller::getByName, Role.USER);
        };
    }

    //________________________________________________________

    private void createResponsibility(Context ctx){
        String name = getPathName(ctx);

        Responsibility r = Responsibility.builder()
            .name(name)
            .build();

        responsibilityDAO.create(r);

        String message = messageService.buildMessage(Notifications.CREATED, "Responsibility");

        respond(ctx, 200, message, Map.of("data", responsibilityMapper.toDTO(r)));
    }

    //________________________________________________________

    private void updateResponsibility(Context ctx){
        String oldResponsibility = ErrorHandler.tryString(
            ctx.pathParam("old"),
            Notifications.ENTER_NAME.getDisplayName()
        );

        String newResponsibility = ErrorHandler.tryString(
            ctx.pathParam("new"),
            Notifications.ENTER_NAME.getDisplayName()
        );

        Responsibility responsibility = ErrorHandler.tryEntity(
            responsibilityDAO.getByName(oldResponsibility),
            messageService.buildMessage(
                Notifications.NOT_FOUND_WITH_NAME,
                "Responsibility",
                oldResponsibility
            )
        );

        responsibility.setName(newResponsibility);

        responsibilityDAO.update(responsibility);

        String message = messageService.buildMessage(Notifications.UPDATED, "Responsibility");

        respond(ctx, 200, message, Map.of("data", responsibilityMapper.toDTO(responsibility)));
    }

    //________________________________________________________

    private void deleteResponsibility(Context ctx){
        String name = getPathName(ctx);

        Responsibility responsibility = ErrorHandler.tryEntity(
            responsibilityDAO.getByName(name),
            messageService.buildMessage(
                Notifications.RESPONSIBILITY_NOT_FOUND,
                name
            )

        );

        responsibilityDAO.deleteById(responsibility.getId());

        String message = messageService.buildMessage(
            Notifications.RESPONSIBILITY_DELETED,
            name
        );

        respond(ctx, 200, message, null);
    }

    //________________________________________________________

    private void getByName(Context ctx){

        String name = getPathName(ctx);

        Responsibility responsibility = ErrorHandler.tryEntity(
            responsibilityDAO.getByName(name),
            messageService.buildMessage(
                Notifications.RESPONSIBILITY_NOT_FOUND,
                name
            )
        );

        String message = messageService.buildMessage(
            Notifications.GET_BY_NAME,
            "responsibility",
            name
        );

        respond(ctx, 200, message, Map.of("data", responsibilityMapper.toDTO(responsibility)));
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