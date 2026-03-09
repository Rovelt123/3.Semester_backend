package app.controllers;

import app.Main;
import app.controllers.Generic.BaseController;
import app.daos.UserDAO;
import app.dtos.UserDTO;
import app.entities.User;
import app.enums.Notifications;
import app.enums.Role;
import app.services.MessageService;
import app.services.PasswordService;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;
import java.util.Map;

public class UserController extends BaseController<User, UserDTO> {

    private static final UserDAO userDAO = Main.setup.getUserDAO();

    public UserController() {
        super(User.class, UserDTO::new);
    }

    // ________________________________________________________

    public static void registerRoutes(Javalin app) {
        UserController controller = new UserController();

        // Auth endpoints
        app.post("/auth/register", controller::createUser);
        app.post("/auth/login", controller::login);
        app.post("/auth/logout", controller::logout);

        // User endpoints
        app.put("/user/update-username", controller::updateUsername);
        app.put("/user/update-password", controller::updatePassword);
        app.delete("/user/delete", controller::deleteUserWithConfirm);
        app.get("/users", controller::getAll);
        app.get("/user/{id}", controller::getByID);
        app.get("/user/by-username/{username}", controller::getByUsername);

        // Admin endpoints
        app.delete("/user/force-delete/{id}", controller::forceDeleteUser);
    }

    // ________________________________________________________

    private void login(Context ctx) {
        Map<String, String> body = ctx.bodyAsClass(Map.class);
        String username = body.get("username");
        String password = body.get("password");

        User user = userDAO.getByUsername(username);
        if (user == null) {
            ctx.json(Notifications.WRONG_USERNAME.getDisplayName());
            return;
        }

        if (!PasswordService.equals(password, user.getPassword())) {
            ctx.json(Notifications.WRONG_PASSWORD.getDisplayName());
            return;
        }

        ctx.sessionAttribute("user", user);

        String message = MessageService.buildMessage(
                Notifications.LOGGED_IN,
                user.getName()
        );

        ctx.json(Map.of(
                "message", message,
                "user", new UserDTO(user)
        ));
    }

    // ________________________________________________________

    private void logout(Context ctx) {
        ctx.sessionAttribute("user", null);

        ctx.json(Notifications.LOGGED_OUT.getDisplayName());
    }

    // ________________________________________________________

    private void createUser(Context ctx) {
        Map<String, String> body = ctx.bodyAsClass(Map.class);
        String username = body.get("username");
        if((username == null || username.isEmpty())) {
            ctx.status(400).json(Notifications.REGISTER_NO_USERNAME.getDisplayName());
            return;
        }
        String password = body.get("password");
        if((password == null || password.isEmpty())) {
            ctx.status(400).json(Notifications.REGISTER_NO_PASSWORD.getDisplayName());
            return;
        }
        String password_repeat = body.get("password_repeat");
        if((password_repeat == null || password_repeat.isEmpty())) {
            ctx.status(400).json(Notifications.REGISTER_NO_PASSWORD_REPEAT.getDisplayName());
            return;
        }
        if(!password.equals(password_repeat)){
            ctx.status(400).json(Notifications.REGISTER_PASSWORD_MISMATCH.getDisplayName());
            return;
        }
        String name = body.get("name");
        if((name == null || name.isEmpty())) {
            ctx.status(400).json(Notifications.REGISTER_NO_NAME.getDisplayName());
            return;
        }
        Role role = Role.USER;

        if (userDAO.existByColumn(username, "username")) {
            String message = MessageService.buildMessage(Notifications.USERNAME_EXISTS, username);
            ctx.status(400).json(message);
            return;
        }

        User user = new User(name, role, username, PasswordService.hashHelper(password));
        userDAO.create(user);

        String message = MessageService.buildMessage(Notifications.REGISTER_SUCCESS, user.getUsername());

        ctx.status(201).json(Map.of(
                "message", message,
                "user", new UserDTO(user)

        ));
    }

    // ________________________________________________________

    private void updateUsername(Context ctx) {
        Map<String,String> body = ctx.bodyAsClass(Map.class);
        User user = ctx.sessionAttribute("user");
        String newUsername = body.get("newUsername");
        String password = body.get("password");

        if(user == null){
            ctx.status(401).json(Notifications.NOT_LOGGED_IN.getDisplayName());
            return;
        }

        if(!PasswordService.equals(password, user.getPassword())){
            ctx.status(401).json(Notifications.WRONG_PASSWORD.getDisplayName());
            return;
        }

        if(userDAO.existByColumn(newUsername, "username")){
            String message = MessageService.buildMessage(Notifications.USERNAME_EXISTS, newUsername);
            ctx.status(400).json(message);
            return;
        }

        user.setUsername(newUsername);
        userDAO.update(user);

        String message = MessageService.buildMessage(Notifications.USERNAME_UPDATED, user.getUsername());

        ctx.json(message);
    }

    // ________________________________________________________

    private void updatePassword(Context ctx) {
        Map<String,String> body = ctx.bodyAsClass(Map.class);
        User user = ctx.sessionAttribute("user");

        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");
        String newPassword_repeat = body.get("newPassword_repeat");

        if(user == null){
            ctx.status(401).json(Notifications.NOT_LOGGED_IN.getDisplayName());
            return;
        }

        if(!PasswordService.equals(oldPassword, user.getPassword())){
            ctx.status(401).json(Notifications.WRONG_PASSWORD.getDisplayName());
            return;
        }

        if(!newPassword.equals(newPassword_repeat)){
            ctx.status(400).json(Notifications.REGISTER_PASSWORD_MISMATCH.getDisplayName());
            return;
        }

        user.setPassword(PasswordService.hashHelper(newPassword));
        userDAO.update(user);
        ctx.json(Notifications.PASSWORD_UPDATED.getDisplayName());
    }

    // ________________________________________________________

    private void deleteUserWithConfirm(Context ctx) {
        Map<String,String> body = ctx.bodyAsClass(Map.class);
        User user = ctx.sessionAttribute("user");

        if(user == null){
            ctx.status(401).json(Notifications.NOT_LOGGED_IN.getDisplayName());
            return;
        }

        String password1 = body.get("password1");
        String password2 = body.get("password2");

        if(!password1.equals(password2) || !PasswordService.equals(password1, user.getPassword())){
            ctx.status(400).json(Notifications.REGISTER_PASSWORD_MISMATCH.getDisplayName());
            return;
        }
        String username =  user.getUsername();
        userDAO.deleteById(user.getId());

        ctx.sessionAttribute("user", null);

        String message = MessageService.buildMessage(Notifications.DELETE_USER_SUCESS, username);

        ctx.json(message);
    }

    // ________________________________________________________

    private void forceDeleteUser(Context ctx) {
        User admin = ctx.sessionAttribute("user");
        int targetId = Integer.parseInt(ctx.pathParam("id"));

        if(admin == null || admin.getRole() != Role.CHEF){
            ctx.status(403).json(Notifications.ADMINS_ONLY.getDisplayName());
            return;
        }

        User target = userDAO.getById(targetId);
        if(target == null){

            String message = MessageService.buildMessage(Notifications.USER_NOT_FOUND_ID, String.valueOf(targetId));
            ctx.status(404).json(message);
            return;
        }

        Map<String,String> body = ctx.bodyAsClass(Map.class);
        String confirmUsername = body.get("confirm_username");

        if (confirmUsername == null || !confirmUsername.equals(target.getUsername())) {
            ctx.status(400).json(Notifications.USERNAME_CONFIRM_MISMATCH.getDisplayName());
            return;
        }

        String message = MessageService.buildMessage(Notifications.DELETE_USER_SUCESS, target.getName());
        userDAO.delete(target);
        ctx.json(message);
    }

    // ________________________________________________________

    private void getByUsername(Context ctx){
        String username = ctx.pathParam("username");
        User user = userDAO.getByUsername(username);
        if(user == null){
            String message = MessageService.buildMessage(Notifications.USER_NOT_FOUND_USERNAME, username);
            ctx.status(404).json(message);
            return;
        }
        ctx.json(new UserDTO(user));
    }

    // ________________________________________________________

    @Override
    protected List<User> getAllEntities() {
        return userDAO.getAll();
    }

    @Override
    protected User getEntityById(int id) {
        return userDAO.getById(id);
    }
}