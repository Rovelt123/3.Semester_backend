package app.controllers;

import app.Main;
import app.controllers.Generic.BaseController;
import app.daos.UserDAO;
import app.dtos.ShiftDTO;
import app.dtos.UserDTO;
import app.entities.Shift;
import app.entities.User;
import app.enums.Notifications;
import app.services.MessageService;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserController extends BaseController<User, UserDTO> {

    private static final UserDAO userDAO = Main.setup.getUserDAO();

    public UserController() {
        super(User.class, UserDTO::new);
    }

    public static void registerRoutes(Javalin app) {

        UserController controller = new UserController();

        //POST
        app.post("/user", UserController::createUser);

        //DELETE
        app.delete("/user/{id}", UserController::deleteUser);

        //PUT
        app.put("/user", UserController::updateUser);

        //GET
        app.get("/user", controller::getAll);
        app.get("/user/{id}", controller::getByID);
        //app.get("/user", UserController::getAll);
        //app.get("/user/{id}", UserController::getByID);
    }


    // ________________________________________________________

    private static void createUser(Context ctx) {

    }

    // ________________________________________________________

    private static void deleteUser(Context ctx) {

    }

    // ________________________________________________________

    private static void updateUser(Context ctx) {

    }

    // ________________________________________________________

    @Override
    protected List<User> getAllEntities() {
        return userDAO.getAll();
    }

    // ________________________________________________________

    @Override
    protected User getEntityById(int id) {
        return userDAO.getById(id);
    }

    // ________________________________________________________

    private static UserDTO convertUserDTO(User user) {
        return new UserDTO(user);
    }

}
