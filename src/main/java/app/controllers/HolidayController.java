package app.controllers;

import app.Main;
import app.controllers.Generic.BaseController;
import app.daos.HolidayDAO;
import app.dtos.HolidayDTO;
import app.entities.Holiday;
import app.entities.User;
import app.enums.Role;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class HolidayController extends BaseController<Holiday, HolidayDTO> {

    private static final HolidayDAO holidayDAO = Main.setup.getHolidayDAO();

    public HolidayController(){
        super(Holiday.class, HolidayDTO::new);
    }

    public static void registerRoutes(Javalin app){

        HolidayController controller = new HolidayController();

        app.post("/holiday", controller::requestHoliday);
        app.put("/holiday/{id}", controller::updateHoliday);
        app.post("/holiday/{id}/approve", controller::approveHoliday);
        app.post("/holiday/{id}/reject", controller::rejectHoliday);

        app.get("/holidays", controller::getAll);
    }

    private void requestHoliday(Context ctx){

        User user = ctx.sessionAttribute("user");

        Map<String,String> body = ctx.bodyAsClass(Map.class);

        LocalDate start = LocalDate.parse(body.get("start"));
        LocalDate end = LocalDate.parse(body.get("end"));

        Holiday holiday = new Holiday(user, start, end);

        holidayDAO.create(holiday);

        ctx.status(201).json(new HolidayDTO(holiday));
    }

    private void updateHoliday(Context ctx){

        User user = ctx.sessionAttribute("user");

        int id = Integer.parseInt(ctx.pathParam("id"));

        Holiday holiday = holidayDAO.getById(id);

        if(holiday.getUser().getId() != user.getId()){
            ctx.status(403);
            return;
        }

        Map<String,String> body = ctx.bodyAsClass(Map.class);

        holiday.setStartDate(LocalDate.parse(body.get("start")));
        holiday.setEndDate(LocalDate.parse(body.get("end")));

        holidayDAO.update(holiday);

        ctx.json(new HolidayDTO(holiday));
    }

    private void approveHoliday(Context ctx){

        User admin = ctx.sessionAttribute("user");

        if(admin.getRole() != Role.CHEF){
            ctx.status(403);
            return;
        }

        int id = Integer.parseInt(ctx.pathParam("id"));

        Holiday holiday = holidayDAO.getById(id);

        holiday.approve();

        holidayDAO.update(holiday);

        ctx.json(new HolidayDTO(holiday));
    }

    private void rejectHoliday(Context ctx){

        User admin = ctx.sessionAttribute("user");

        if(admin.getRole() != Role.CHEF){
            ctx.status(403);
            return;
        }

        int id = Integer.parseInt(ctx.pathParam("id"));

        Holiday holiday = holidayDAO.getById(id);

        holiday.reject();

        holidayDAO.update(holiday);

        ctx.json(new HolidayDTO(holiday));
    }

    @Override
    protected List<Holiday> getAllEntities() {
        return holidayDAO.getAll();
    }

    @Override
    protected Holiday getEntityById(int id) {
        return holidayDAO.getById(id);
    }
}