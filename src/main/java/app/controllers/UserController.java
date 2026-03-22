package app.controllers;


import app.Main;
import app.controllers.Generic.BaseController;
import app.daos.ResponsibilityDAO;
import app.daos.UserDAO;
import app.dtos.UserDTO;
import app.entities.Responsibility;
import app.entities.User;
import app.enums.Notifications;
import app.enums.Role;
import app.services.MessageService;
import app.services.HashService;
import app.services.TryCatchService;
import app.services.security.SecurityService;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.Context;

import static io.javalin.apibuilder.ApiBuilder.*;

import java.util.List;
import java.util.Map;
import java.util.Set;


public class UserController extends BaseController<User, UserDTO> {

    private static final UserDAO userDAO = Main.setup.getUserDAO();
    private static final ResponsibilityDAO responsibilityDAO = Main.setup.getRespDAO();
    private static final SecurityService securityService = new SecurityService();


    public UserController() {
        super(User.class, UserDTO::new);
    }

    // ________________________________________________________

    public static EndpointGroup registerRoutes() {
        UserController controller = new UserController();
        return ()->{
            post("users/auth/register", controller::createUser, Role.ANYONE);
            post("users/auth/login", controller::login, Role.ANYONE);

            // User endpoints
            put("/user/username", controller::updateUsername, Role.USER);
            put("/user/password", controller::updatePassword, Role.USER);
            delete("/user/delete", controller::deleteUserWithConfirm, Role.USER);
            get("/users", controller::getAll, Role.USER);
            get("/user/{id}", controller::getByID, Role.USER);
            get("/user/by-username/{username}", controller::getByUsername, Role.USER);
            get("/users/responsibility/{responsibility}", controller::getUsersWithResponsibility, Role.USER);
            get("/users/role/{role}", controller::getUsersWithRole, Role.USER);

            // Admin endpoints
            delete("/user/force-delete/{id}/{confirm_name}", controller::forceDeleteUser, Role.CHEF);
            delete("/user/{id}/roles/{role}", controller::removeRole , Role.CHEF);
            put("/user/{id}/roles/{role}", controller::addRole , Role.CHEF);
            put("/user/{id}/responsibility/{responsibility}", controller::addResponsibility , Role.CHEF);
            delete("/user/{id}/responsibility/{responsibility}", controller::removeResponsibility , Role.CHEF);
        };
    }

    // ________________________________________________________

    private void login(Context ctx) {
        Map<String, String> body = TryCatchService.tryBodyMap(ctx, Notifications.BODY_EMPTY.getDisplayName());
        User user = TryCatchService.tryEntity(
                userDAO.getByUsername(body.get("username")),
                Notifications.WRONG_USERNAME.getDisplayName()
        );

        if (!HashService.hashEquals(body.get("password"), user.getPassword())) {
            respond(ctx, 401, Notifications.WRONG_PASSWORD.getDisplayName(), null);
            return;
        }

        String token = securityService.createToken(new UserDTO(user));

        String message = MessageService.buildMessage(
                Notifications.LOGGED_IN,
                user.getFirstname()
        );

        respond(ctx, 200, message, Map.of(
            "token", token,
            "data", new UserDTO(user)
        ));
    }

    // ________________________________________________________

    private void createUser(Context ctx) {
        Map<String, String> body = TryCatchService.tryBodyMap(ctx, Notifications.BODY_EMPTY.getDisplayName());

        String firstname = TryCatchService.tryString(body.get("first_name"), Notifications.REGISTER_NO_FIRSTNAME.getDisplayName());
        String lastname = TryCatchService.tryString(body.get("last_name"), Notifications.REGISTER_NO_LASTNAME.getDisplayName());
        String username = TryCatchService.tryString(body.get("username"), Notifications.REGISTER_NO_USERNAME.getDisplayName());
        String password = TryCatchService.tryString(body.get("password"), Notifications.REGISTER_NO_PASSWORD.getDisplayName());
        String password_repeat = TryCatchService.tryString(body.get("repeat_password"), Notifications.REGISTER_NO_PASSWORD_REPEAT.getDisplayName());

        if(!password.equals(password_repeat)){
            ctx.status(400).json(Notifications.REGISTER_PASSWORD_MISMATCH.getDisplayName());
            return;
        }

        Role role = Role.USER;

        if (userDAO.existByColumn(username, "username")) {
            String message = MessageService.buildMessage(Notifications.USERNAME_EXISTS, username);
            ctx.status(400).json(message);
            return;
        }

        User user = TryCatchService.tryEntity(
            userDAO.create(new User(firstname, lastname, Set.of(role), username, HashService.hashHelper(password))),
            MessageService.buildMessage(Notifications.USERNAME_EXISTS, username)
        );

        //Make sure to make responses for new users - Otherwise new users can't take older shift requests!
        ShiftRequestController.checkActiveShiftRequests(user);

        String token = securityService.createToken(new UserDTO(user));

        String message = MessageService.buildMessage(Notifications.REGISTER_SUCCESS, user.getUsername());

        respond(ctx, 201, message, Map.of(
            "token", token,
            "data", new UserDTO(user)
        ));
    }

    // ________________________________________________________

    private void updateUsername(Context ctx) {
        Map<String, String> body = TryCatchService.tryBodyMap(ctx, Notifications.BODY_EMPTY.getDisplayName());

        User user = getAuthenticatedUser(ctx);

        String newUsername = TryCatchService.tryString(body.get("new_username"), Notifications.REGISTER_NO_USERNAME.getDisplayName());
        String password = TryCatchService.tryString(body.get("password"), Notifications.REGISTER_NO_PASSWORD.getDisplayName());

        if(!HashService.hashEquals(password, user.getPassword())){
            respond(ctx, 401, Notifications.WRONG_PASSWORD.getDisplayName(), null);
            return;
        }

        user.setUsername(newUsername);
        userDAO.update(user);

        String message = MessageService.buildMessage(Notifications.USERNAME_UPDATED, user.getUsername());
        respond(ctx, 200, message, null);
    }

    // ________________________________________________________

    private void updatePassword(Context ctx) {

        Map<String, String> body = TryCatchService.tryBodyMap(ctx, Notifications.BODY_EMPTY.getDisplayName());

        User user = getAuthenticatedUser(ctx);

        String oldPassword = TryCatchService.tryString(body.get("old_password"), Notifications.REGISTER_NO_PASSWORD.getDisplayName());
        String newPassword = TryCatchService.tryString(body.get("new_password"), Notifications.UPDATE_PASSWORD_NO_NEWPASSWORD.getDisplayName());
        String newPassword_repeat = TryCatchService.tryString(body.get("new_password_repeat"), Notifications.UPDATE_PASSWORD_NO_NEWPASSWORD_REPEAT.getDisplayName());

        if(!HashService.hashEquals(oldPassword, user.getPassword())){
            respond(ctx, 401, Notifications.WRONG_PASSWORD.getDisplayName(), null);
            return;
        }

        if(!newPassword.equals(newPassword_repeat)){
            respond(ctx, 400, Notifications.REGISTER_PASSWORD_MISMATCH.getDisplayName(), null);
            return;
        }

        user.setPassword(HashService.hashHelper(newPassword));
        userDAO.update(user);

        respond(ctx, 200, Notifications.PASSWORD_UPDATED.getDisplayName(), null);
    }

    // ________________________________________________________

    private void deleteUserWithConfirm(Context ctx) {

        Map<String, String> body = TryCatchService.tryBodyMap(ctx, Notifications.BODY_EMPTY.getDisplayName());

        UserDTO userDTO = TryCatchService.tryEntity(ctx.attribute("user"), Notifications.NOT_LOGGED_IN.getDisplayName());
        User user = TryCatchService.tryEntity(
                userDAO.getById(userDTO.getId()),
                MessageService.buildMessage(Notifications.USER_NOT_FOUND_ID, String.valueOf(userDTO.getId()))
        );


        String password = TryCatchService.tryString(body.get("password"), Notifications.REGISTER_NO_PASSWORD.getDisplayName());

        if(!HashService.hashEquals(password, user.getPassword())){
            respond(ctx, 400, Notifications.REGISTER_PASSWORD_MISMATCH.getDisplayName(), null);
            return;
        }

        String username =  user.getUsername();
        userDAO.deleteById(user.getId());

        String message = MessageService.buildMessage(Notifications.DELETE_USER_SUCESS, username);

        respond(ctx, 200, message, null);
    }

    // ________________________________________________________

    private void forceDeleteUser(Context ctx) {
        int targetId = getPathId(ctx);

        User target = TryCatchService.tryEntity(userDAO.getById(targetId), MessageService.buildMessage(Notifications.USER_NOT_FOUND_ID, String.valueOf(targetId)));


        String confirmUsername = TryCatchService.tryString(ctx.pathParam("confirm_name"), Notifications.USERNAME_CONFIRM_MISMATCH.getDisplayName());

        if (!target.equals(confirmUsername)) {
            String message = MessageService.buildMessage(Notifications.DELETE_USER_MISMATCH, target.getFirstname(), confirmUsername);
            respond(ctx, 400, message, null);
            return;
        }


        String message = MessageService.buildMessage(Notifications.DELETE_USER_SUCESS, target.getFirstname());
        userDAO.delete(target);
        respond(ctx, 200, message, null);
    }

    // ________________________________________________________

    private void getByUsername(Context ctx){
        String username = ctx.pathParam("username");
        User user = TryCatchService.tryEntity(
            userDAO.getByUsername(username),
            MessageService.buildMessage(Notifications.USER_NOT_FOUND_USERNAME, username)
        );

        String message = MessageService.buildMessage(
            Notifications.GET_BY_NAME,
            "user",
            username
        );

        respond(ctx, 200, message, Map.of("data", new UserDTO(user)));
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

    private void addRole(Context ctx) {
        User user = getUserByID(ctx);

        Role role = TryCatchService.tryParseEnum(Role.class, ctx.pathParam("role"), MessageService.buildMessage(Notifications.ROLE_NOT_FOUND, ctx.pathParam("role")));

        user.addRole(role);
        userDAO.update(user);

        String message = MessageService.buildMessage(Notifications.ROLE_ADDED_USER, role.getDisplayName(), user.getFirstname());

        respond(ctx, 200, message, null);
    }

    // ________________________________________________________

    private void removeRole(Context ctx) {
        User user = getUserByID(ctx);

        Role role = TryCatchService.tryParseEnum(Role.class, ctx.pathParam("role"), MessageService.buildMessage(Notifications.ROLE_NOT_FOUND, ctx.pathParam("role")));

        user.removeRole(role);
        userDAO.update(user);

        String message = MessageService.buildMessage(Notifications.ROLE_REMOVED_USER, role.getDisplayName(), user.getFirstname());

        respond(ctx, 200, message, null);
    }

    // ________________________________________________________

    private void addResponsibility(Context ctx) {
        User user = getUserByID(ctx);

        Responsibility responsibility = TryCatchService.tryEntity(
            responsibilityDAO.getByName(ctx.pathParam("responsibility")),
            MessageService.buildMessage(Notifications.RESPONSIBILITY_NOT_FOUND, ctx.pathParam("responsibility"))
        );

        user.addResponsibility(responsibility);
        userDAO.update(user);

        String message = MessageService.buildMessage(Notifications.RESPONSIBILITY_ADDED_USER, responsibility.getName(), user.getFirstname());

        respond(ctx, 200, message, null);
    }

    // ________________________________________________________

    private void removeResponsibility(Context ctx) {
        User user = getUserByID(ctx);

        Responsibility responsibility = TryCatchService.tryEntity(
                responsibilityDAO.getByName(ctx.pathParam("responsibility")),
                MessageService.buildMessage(Notifications.RESPONSIBILITY_NOT_FOUND, ctx.pathParam("responsibility"))
        );

        user.removeResponsibility(responsibility);
        userDAO.update(user);

        String message = MessageService.buildMessage(Notifications.RESPONSIBILITY_REMOVED_USER, responsibility.getName(), user.getFirstname());

        respond(ctx, 200, message, null);
    }

    // ________________________________________________________
    //TODO:
    // getUsersWithResponsibility
    // getUsersWithRole

    private void getUsersWithResponsibility(Context ctx) {

        String name = TryCatchService.tryString(
                ctx.pathParam("responsibility"),
                Notifications.FIELD_EMPTY.getDisplayName()
        );

        List<UserDTO> users = userDAO.getUsersByResponsibility(name)
                .stream()
                .map(UserDTO::new)
                .toList();

        if(users.isEmpty()){
            ctx.status(200).json(MessageService.buildMessage(
                    Notifications.GET_ALL_EMPTY,
                    name
            ));
            return;
        }

        String message = MessageService.buildMessage(
          Notifications.GET_USERS_RESPONSIBILITY,
          name
        );

        respond(ctx, 200, message, Map.of("data", users));
    }
    
    // ________________________________________________________

    private void getUsersWithRole(Context ctx) {

        Role role = TryCatchService.tryParseEnum(
                Role.class,
                ctx.pathParam("role"),
                MessageService.buildMessage(Notifications.ROLE_NOT_FOUND, ctx.pathParam("role"))
        );

        List<UserDTO> users = userDAO.getUsersByRole(role)
                .stream()
                .map(UserDTO::new)
                .toList();

        if(users.isEmpty()){
            ctx.status(200).json(MessageService.buildMessage(
                    Notifications.GET_ALL_EMPTY,
                    role.getDisplayName()
            ));
            return;
        }

        String message = MessageService.buildMessage(
            Notifications.GET_USERS_ROLE,
            String.valueOf(role)
        );

        respond(ctx, 200, message, Map.of("data", users));
    }
}