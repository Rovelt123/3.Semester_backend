package app.controllers;

import app.Main;
import app.controllers.Generic.BaseController;
import app.daos.HolidayDAO;
import app.daos.UserDAO;
import app.dtos.HolidayDTO;
import app.dtos.UserDTO;
import app.entities.Holiday;
import app.entities.User;
import app.enums.Role;

import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.Context;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static io.javalin.apibuilder.ApiBuilder.*;

public class HolidayController extends BaseController<Holiday, HolidayDTO> {

    private static final HolidayDAO holidayDAO = Main.setup.getHolidayDAO();
    private static final UserDAO userDAO = Main.setup.getUserDAO();

    //________________________________________________________

    public HolidayController(){
        super(Holiday.class, HolidayDTO::new);
    }

    //________________________________________________________

    public static EndpointGroup registerRoutes(){

        HolidayController controller = new HolidayController();

        return () -> {
            post("/holiday", controller::requestHoliday, Role.USER);
            put("/holiday/{id}", controller::updateHoliday, Role.USER);
            post("/holiday/{id}/approve", controller::approveHoliday, Role.CHEF);
            post("/holiday/{id}/reject", controller::rejectHoliday, Role.CHEF);

            get("/holidays", controller::getAll, Role.USER);
        };
    }

    //________________________________________________________

    private void requestHoliday(Context ctx){

        UserDTO userDTO = ctx.attribute("user");
        User user = userDAO.getById(userDTO.getId());

        Map<String,String> body = ctx.bodyAsClass(Map.class);

        LocalDate start = LocalDate.parse(body.get("start"));
        LocalDate end = LocalDate.parse(body.get("end"));

        Holiday holiday = new Holiday(user, start, end);

        holidayDAO.create(holiday);

        ctx.status(201).json(new HolidayDTO(holiday));
    }

    //________________________________________________________

    private void updateHoliday(Context ctx){

        UserDTO userDTO = ctx.attribute("user");
        User user = userDAO.getById(userDTO.getId());

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

    //________________________________________________________

    private void approveHoliday(Context ctx){


        UserDTO admin = ctx.attribute("user");

        if(admin.getRoles().stream().anyMatch(role -> role.equals(Role.CHEF))){
            ctx.status(403);
            return;
        }

        int id = Integer.parseInt(ctx.pathParam("id"));

        Holiday holiday = holidayDAO.getById(id);

        holiday.approve();

        holidayDAO.update(holiday);

        ctx.json(new HolidayDTO(holiday));
    }

    //________________________________________________________

    private void rejectHoliday(Context ctx){

        UserDTO admin = ctx.attribute("user");

        if(admin.getRoles().stream().anyMatch(role -> role.equals(Role.CHEF))){
            ctx.status(403);
            return;
        }

        int id = Integer.parseInt(ctx.pathParam("id"));

        Holiday holiday = holidayDAO.getById(id);

        holiday.reject();

        holidayDAO.update(holiday);

        ctx.json(new HolidayDTO(holiday));
    }

    //________________________________________________________

    @Override
    protected List<Holiday> getAllEntities() {
        return holidayDAO.getAll();
    }

    //________________________________________________________

    @Override
    protected Holiday getEntityById(int id) {
        return holidayDAO.getById(id);
    }
}