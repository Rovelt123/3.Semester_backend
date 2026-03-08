package app.controllers;

import app.Main;
import app.controllers.Generic.BaseController;
import app.daos.ResponsibilityDAO;
import app.dtos.ResponsibilityDTO;
import app.entities.Responsibility;
import io.javalin.Javalin;
import java.util.List;

public class ResponsibilityController extends BaseController<Responsibility, ResponsibilityDTO> {

    private static final ResponsibilityDAO responsibilityDao = Main.setup.getRespDAO();

    // ________________________________________________________

    public ResponsibilityController() {
        super(Responsibility.class, ResponsibilityDTO::new);
    }

    // ________________________________________________________

    public static void registerRoutes(Javalin app) {
        ResponsibilityController controller = new ResponsibilityController();

        //GET
        app.get("/responsibilities", controller::getAll);
        app.get("/responsibility/{id}", controller::getByID);
    }

    // ________________________________________________________

    @Override
    protected List<Responsibility> getAllEntities() {
        return responsibilityDao.getAll();
    }

    // ________________________________________________________

    @Override
    protected Responsibility getEntityById(int id) {
        return responsibilityDao.getById(id);
    }

    // ________________________________________________________
}
