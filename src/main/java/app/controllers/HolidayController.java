package app.controllers;

import app.Main;
import app.controllers.Generic.BaseController;
import app.daos.HolidayDAO;
import app.dtos.HolidayDTO;
import app.entities.Holiday;
import io.javalin.Javalin;

import java.util.List;

public class HolidayController extends BaseController<Holiday, HolidayDTO> {

    private static final HolidayDAO holidayDAO = Main.setup.getHolidayDAO();

    // ________________________________________________________

    public HolidayController() {
        super(Holiday.class, HolidayDTO::new);
    }

    // ________________________________________________________

    public static void registerRoutes(Javalin app) {
        HolidayController controller = new HolidayController();

        app.get("/holidays", controller::getAll);
        app.get("/holiday/{id}", controller::getByID);

    }

    // ________________________________________________________

    @Override
    protected List<Holiday> getAllEntities() {
        return holidayDAO.getAll();
    }

    // ________________________________________________________

    @Override
    protected Holiday getEntityById(int id) {
        return holidayDAO.getById(id);
    }
}
