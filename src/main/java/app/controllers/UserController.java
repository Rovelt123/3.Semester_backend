package app.controllers;

import app.controllers.Generic.BaseController;
import app.dtos.UserDTO;
import app.entities.Responsibility;
import app.entities.User;
import app.enums.Notifications;
import app.enums.Role;
import app.services.HashService;
import app.utils.ErrorHandler;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.Context;
import static io.javalin.apibuilder.ApiBuilder.*;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class UserController extends BaseController<User, UserDTO> {

    public UserController() {
        super(User.class, userMapper);
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
            put("/user/{id}", controller::update, Role.USER);
            delete("/user/delete", controller::deleteUserWithConfirm, Role.USER);
            get("/users", controller::getAll, Role.USER);
            get("/user/{id}", controller::getByID, Role.USER);
            get("/user/by-username/{username}", controller::getByUsername, Role.USER);
            get("/users/responsibility/{responsibility}", controller::getUsersWithResponsibility, Role.USER);
            get("/users/role/{role}", controller::getUsersWithRole, Role.USER);

            // Admin endpoints
            delete("/user/force-delete/{id}/{confirm_name}", controller::forceDeleteUser, Role.CHEF);
            delete("/user/{user_id}/roles/{role}", controller::removeRole , Role.CHEF);
            put("/user/{user_id}/roles/{role}", controller::addRole , Role.CHEF);
            put("/user/{user_id}/responsibility/{responsibility}", controller::addResponsibility , Role.CHEF);
            delete("/user/{user_id}/responsibility/{responsibility}", controller::removeResponsibility , Role.CHEF);
        };
    }

    // ________________________________________________________

    private void login(Context ctx) {
        Map<String, String> body = ErrorHandler.tryBodyMap(ctx, Notifications.BODY_EMPTY.getDisplayName());
        User user = ErrorHandler.tryEntity(
            userDAO.getByUsername(body.get("username")),
            Notifications.WRONG_USERNAME.getDisplayName()
        );

        if (!HashService.hashEquals(body.get("password"), user.getPassword())) {
            respond(ctx, 401, Notifications.WRONG_PASSWORD.getDisplayName(), null);
            return;
        }

        UserDTO dto = userMapper.toDTO(user);

        String token = securityService.createToken(dto);

        String message = messageService.buildMessage(
                Notifications.LOGGED_IN,
                user.getFirstname()
        );

        respond(ctx, 200, message, Map.of(
            "token", token,
            "data", dto
        ));
    }

    // ________________________________________________________

    private void createUser(Context ctx) {
        Map<String, String> body = ErrorHandler.tryBodyMap(ctx, Notifications.BODY_EMPTY.getDisplayName());

        String firstname = ErrorHandler.tryString(body.get("first_name"), Notifications.REGISTER_NO_FIRSTNAME.getDisplayName());
        String lastname = ErrorHandler.tryString(body.get("last_name"), Notifications.REGISTER_NO_LASTNAME.getDisplayName());
        String username = ErrorHandler.tryString(body.get("username"), Notifications.REGISTER_NO_USERNAME.getDisplayName());
        String password = ErrorHandler.tryString(body.get("password"), Notifications.REGISTER_NO_PASSWORD.getDisplayName());
        String password_repeat = ErrorHandler.tryString(body.get("repeat_password"), Notifications.REGISTER_NO_PASSWORD_REPEAT.getDisplayName());

        if(!password.equals(password_repeat)){
            ctx.status(400).json(Notifications.REGISTER_PASSWORD_MISMATCH.getDisplayName());
            return;
        }

        Role role = Role.USER;

        if (userDAO.existByColumn(username, "username")) {
            String message = messageService.buildMessage(Notifications.USERNAME_EXISTS, username);
            ctx.status(400).json(message);
            return;
        }

        User user = ErrorHandler.tryEntity(
            userDAO.create(User.builder()
                .firstname(firstname)
                .lastname(lastname)
                .roles(Set.of(role))
                .username(username)
                .password(HashService.hashHelper(password))
                .build()),
            messageService.buildMessage(Notifications.USERNAME_EXISTS, username)
        );

        //Make sure to make responses for new users - Otherwise new users can't take older shift requests!
        ShiftRequestController shiftRequestController = new ShiftRequestController();
        threadService.runAsync(() -> shiftRequestController.checkActiveShiftRequests(user));

        UserDTO dto = userMapper.toDTO(user);

        String token = securityService.createToken(dto);

        String message = messageService.buildMessage(Notifications.REGISTER_SUCCESS, user.getUsername());

        respond(ctx, 201, message, Map.of(
            "token", token,
            "data", dto
        ));
    }

    // ________________________________________________________

    private void updateUsername(Context ctx) {
        Map<String, String> body = ErrorHandler.tryBodyMap(ctx, Notifications.BODY_EMPTY.getDisplayName());

        User user = getAuthenticatedUser(ctx);

        String newUsername = ErrorHandler.tryString(body.get("new_username"), Notifications.REGISTER_NO_USERNAME.getDisplayName());
        String password = ErrorHandler.tryString(body.get("password"), Notifications.REGISTER_NO_PASSWORD.getDisplayName());

        if(!HashService.hashEquals(password, user.getPassword())){
            respond(ctx, 401, Notifications.WRONG_PASSWORD.getDisplayName(), null);
            return;
        }

        user.setUsername(newUsername);
        userDAO.update(user);

        String message = messageService.buildMessage(Notifications.USERNAME_UPDATED, user.getUsername());
        respond(ctx, 200, message, null);
    }

    // ________________________________________________________

    private void updatePassword(Context ctx) {

        Map<String, String> body = ErrorHandler.tryBodyMap(ctx, Notifications.BODY_EMPTY.getDisplayName());

        User user = getAuthenticatedUser(ctx);

        String oldPassword = ErrorHandler.tryString(body.get("old_password"), Notifications.REGISTER_NO_PASSWORD.getDisplayName());
        String newPassword = ErrorHandler.tryString(body.get("new_password"), Notifications.UPDATE_PASSWORD_NO_NEWPASSWORD.getDisplayName());
        String newPassword_repeat = ErrorHandler.tryString(body.get("new_password_repeat"), Notifications.UPDATE_PASSWORD_NO_NEWPASSWORD_REPEAT.getDisplayName());

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

    private void update(Context ctx) {
        User loggedIn = getAuthenticatedUser(ctx);

        int id = getPathId(ctx);

        if (loggedIn.getId() != id && !loggedIn.getRoles().contains(Role.CHEF)) {
            respond(ctx, 400, Notifications.NOT_ALLOWED.getDisplayName(), null);
            return;
        }


        User user = ErrorHandler.tryEntity(
            userDAO.getById(id),
            messageService.buildMessage(
                Notifications.NOT_FOUND_ID,
                "User",
                String.valueOf(id)
            )
        );

        Map<String, String> body = ErrorHandler.tryBodyMap(
            ctx,
            Notifications.BODY_EMPTY.getDisplayName()
        );

        if (body.containsKey("firstname"))
            user.setFirstname(ErrorHandler.tryString(
                body.get("firstname"),
                Notifications.REGISTER_NO_FIRSTNAME.getDisplayName()
            ));


        if (body.containsKey("lastname"))
            user.setLastname(ErrorHandler.tryString(
                body.get("lastname"),
                Notifications.REGISTER_NO_LASTNAME.getDisplayName()
            ));

        if (body.containsKey("username")) {
            if (userDAO.existByColumn(body.get("username"), "username")) {
                String message = messageService.buildMessage(Notifications.USERNAME_EXISTS, body.get("username"));
                respond(ctx, 400, message, null);
                return;
            }

            user.setUsername(ErrorHandler.tryString(
                body.get("username"),
                Notifications.REGISTER_NO_LASTNAME.getDisplayName()
            ));
        }

        if(body.containsKey("password")) {
            if (loggedIn.getRoles().contains(Role.CHEF)) {
                user.setPassword(ErrorHandler.tryString(
                    HashService.hashHelper(body.get("password")),
                    Notifications.REGISTER_NO_FIRSTNAME.getDisplayName()
                ));
            } else {
                String oldPassword = ErrorHandler.tryString(body.get("password"), Notifications.REGISTER_NO_PASSWORD.getDisplayName());
                String newPassword = ErrorHandler.tryString(body.get("new_password"), Notifications.UPDATE_PASSWORD_NO_NEWPASSWORD.getDisplayName());
                String newPassword_repeat = ErrorHandler.tryString(body.get("new_password_repeat"), Notifications.UPDATE_PASSWORD_NO_NEWPASSWORD_REPEAT.getDisplayName());

                if(!HashService.hashEquals(oldPassword, user.getPassword())){
                    respond(ctx, 401, Notifications.WRONG_PASSWORD.getDisplayName(), null);
                    return;
                }

                if(!newPassword.equals(newPassword_repeat)){
                    respond(ctx, 400, Notifications.REGISTER_PASSWORD_MISMATCH.getDisplayName(), null);
                    return;
                }

                user.setPassword(HashService.hashHelper(newPassword));
            }
        }

        userDAO.update(user);

        String message = messageService.buildMessage(
                Notifications.USER_UPDATED,
                String.valueOf(id)
        );

        respond(ctx, 200, message, Map.of(
                "data", userMapper.toDTO(user)
        ));
    }

    // ________________________________________________________

    private void deleteUserWithConfirm(Context ctx) {

        Map<String, String> body = ErrorHandler.tryBodyMap(ctx, Notifications.BODY_EMPTY.getDisplayName());

        UserDTO userDTO = ErrorHandler.tryEntity(ctx.attribute("user"), Notifications.NOT_LOGGED_IN.getDisplayName());

        User user = ErrorHandler.tryEntity(
            userMapper.toEntity(userDTO),
            messageService.buildMessage(Notifications.USER_NOT_FOUND_ID, String.valueOf(userDTO.getId()))
        );

        String password = ErrorHandler.tryString(body.get("password"), Notifications.REGISTER_NO_PASSWORD.getDisplayName());

        if(!HashService.hashEquals(password, user.getPassword())){
            respond(ctx, 400, Notifications.REGISTER_PASSWORD_MISMATCH.getDisplayName(), null);
            return;
        }

        String username =  user.getUsername();
        userDAO.deleteById(user.getId());

        String message = messageService.buildMessage(Notifications.DELETE_USER_SUCESS, username);

        respond(ctx, 200, message, null);
    }

    // ________________________________________________________

    private void forceDeleteUser(Context ctx) {
        int targetId = getPathId(ctx);

        User target = ErrorHandler.tryEntity(userDAO.getById(targetId), messageService.buildMessage(Notifications.USER_NOT_FOUND_ID, String.valueOf(targetId)));


        String confirmUsername = ErrorHandler.tryString(ctx.pathParam("confirm_name"), Notifications.USERNAME_CONFIRM_MISMATCH.getDisplayName());

        if (!target.getUsername().equals(confirmUsername)) {
            String message = messageService.buildMessage(Notifications.DELETE_USER_MISMATCH, target.getUsername(), confirmUsername);
            respond(ctx, 400, message, null);
            return;
        }


        String message = messageService.buildMessage(Notifications.DELETE_USER_SUCESS, target.getFirstname());
        userDAO.delete(target);
        respond(ctx, 200, message, null);
    }

    // ________________________________________________________

    private void getByUsername(Context ctx){
        String username = ctx.pathParam("username");
        User user = ErrorHandler.tryEntity(
            userDAO.getByUsername(username),
                messageService.buildMessage(Notifications.USER_NOT_FOUND_USERNAME, username)
        );

        String message = messageService.buildMessage(
            Notifications.GET_BY_NAME,
            "user",
            username
        );

        respond(ctx, 200, message, Map.of("data", userMapper.toDTO(user)));
    }

    // ________________________________________________________

    private void addRole(Context ctx) {
        User user = getUserByID(ctx);

        Role role = ErrorHandler.tryParseEnum(Role.class, ctx.pathParam("role"), messageService.buildMessage(Notifications.ROLE_NOT_FOUND, ctx.pathParam("role")));


        user.getRoles().add(role);
        userDAO.update(user);

        String message = messageService.buildMessage(Notifications.ROLE_ADDED_USER, role.getDisplayName(), user.getFirstname());

        respond(ctx, 200, message, null);
    }

    // ________________________________________________________

    private void removeRole(Context ctx) {
        User user = getUserByID(ctx);

        Role role = ErrorHandler.tryParseEnum(Role.class, ctx.pathParam("role"), messageService.buildMessage(Notifications.ROLE_NOT_FOUND, ctx.pathParam("role")));

        user.getRoles().remove(role);
        userDAO.update(user);

        String message = messageService.buildMessage(Notifications.ROLE_REMOVED_USER, role.getDisplayName(), user.getFirstname());

        respond(ctx, 200, message, null);
    }

    // ________________________________________________________

    private void addResponsibility(Context ctx) {
        User user = getUserByID(ctx);

        Responsibility responsibility = ErrorHandler.tryEntity(
            responsibilityDAO.getByName(ctx.pathParam("responsibility")),
            messageService.buildMessage(Notifications.RESPONSIBILITY_NOT_FOUND, ctx.pathParam("responsibility"))
        );

        user.getResponsibilities().add(responsibility);
        userDAO.update(user);

        String message = messageService.buildMessage(Notifications.RESPONSIBILITY_ADDED_USER, responsibility.getName(), user.getFirstname());

        respond(ctx, 200, message, null);
    }

    // ________________________________________________________

    private void removeResponsibility(Context ctx) {
        User user = getUserByID(ctx);

        Responsibility responsibility = ErrorHandler.tryEntity(
            responsibilityDAO.getByName(ctx.pathParam("responsibility")),
            messageService.buildMessage(Notifications.RESPONSIBILITY_NOT_FOUND, ctx.pathParam("responsibility"))
        );

        user.getResponsibilities().remove(responsibility);
        userDAO.update(user);

        String message = messageService.buildMessage(Notifications.RESPONSIBILITY_REMOVED_USER, responsibility.getName(), user.getFirstname());

        respond(ctx, 200, message, null);
    }

    // ________________________________________________________

    private void getUsersWithResponsibility(Context ctx) {

        String name = ErrorHandler.tryString(
                ctx.pathParam("responsibility"),
                Notifications.FIELD_EMPTY.getDisplayName()
        );

        ErrorHandler.tryEntity(
                responsibilityDAO.getByName(name),
                messageService.buildMessage(
                        Notifications.GET_RESPONSIBILITY_NAME,
                        name
                )
        );

        List<UserDTO> users = userDAO.getUsersByResponsibility(name)
                .stream()
                .map(userMapper::toDTO)
                .toList();

        if(users.isEmpty()){
            ctx.status(200).json(messageService.buildMessage(
                    Notifications.GET_ALL_EMPTY,
                    name
            ));
            return;
        }

        String message = messageService.buildMessage(
                Notifications.GET_USERS_RESPONSIBILITY,
                name
        );

        respond(ctx, 200, message, Map.of("data", users));
    }

    // ________________________________________________________

    private void getUsersWithRole(Context ctx) {

        Role role = ErrorHandler.tryParseEnum(
                Role.class,
                ctx.pathParam("role"),
                messageService.buildMessage(Notifications.ROLE_NOT_FOUND, ctx.pathParam("role"))
        );

        List<UserDTO> users = userDAO.getUsersByRole(role)
                .stream()
                .map(userMapper::toDTO)
                .toList();

        if(users.isEmpty()){
            ctx.status(200).json(messageService.buildMessage(
                    Notifications.GET_ALL_EMPTY,
                    role.getDisplayName()
            ));
            return;
        }

        String message = messageService.buildMessage(
                Notifications.GET_USERS_ROLE,
                String.valueOf(role)
        );

        respond(ctx, 200, message, Map.of("data", users));
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
}