package app.controllers;

import app.Main;
import app.controllers.Generic.BaseController;
import app.daos.MessageDAO;
import app.daos.UserDAO;
import app.dtos.MessageDTO;
import app.dtos.UserDTO;
import app.entities.Message;
import app.entities.User;
import app.enums.Notifications;
import app.enums.Role;
import app.services.MessageService;
import app.services.TryCatchService;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.Context;

import java.util.List;
import java.util.Map;

import static io.javalin.apibuilder.ApiBuilder.*;

public class MessageController extends BaseController<Message, MessageDTO> {

    private static final MessageDAO messageDAO = Main.setup.getMessageDAO();
    private static final UserDAO userDAO = Main.setup.getUserDAO();

    // ________________________________________________________

    public MessageController(){
        super(Message.class, MessageDTO::new);
    }

    // ________________________________________________________

    public static EndpointGroup registerRoutes(){

        MessageController controller = new MessageController();

        return () -> {
            post("/message/{id}", controller::sendMessage, Role.USER);
            put("/message/{id}", controller::updateMessage, Role.USER);
            delete("/message/{id}", controller::deleteMessage, Role.USER);

            get("/messages", controller::getAll, Role.CHEF);
            get("/conversation/{id}", controller::getConversation, Role.USER);
            get("/conversation/admin/{u1}/{u2}", controller::adminGetConversation, Role.USER);
        };
    }

    // ________________________________________________________

    private void sendMessage(Context ctx){

        UserDTO sender = checkLoggedIn(ctx);
        User user = userDAO.getById(sender.getId());

        Map<String,String> body = ctx.bodyAsClass(Map.class);

        String content = body.get("content");

        int id = Integer.parseInt(ctx.pathParam("id"));
        User receiver = userDAO.getById(id);

        Message message = new Message(user, receiver, content);

        messageDAO.create(message);

        ctx.status(201).json(new MessageDTO(message));
    }

    // ________________________________________________________

    private void updateMessage(Context ctx){

        UserDTO user = checkLoggedIn(ctx);

        int id = Integer.parseInt(ctx.pathParam("id"));

        Message message = messageDAO.getById(id);

        if(message.getSender().getId() != user.getId()){
            ctx.status(403);
            return;
        }

        Map<String,String> body = ctx.bodyAsClass(Map.class);

        message.setContent(body.get("content"));

        messageDAO.update(message);

        ctx.json(new MessageDTO(message));
    }

    // ________________________________________________________

    private void deleteMessage(Context ctx){

        User user = TryCatchService.tryEntity(
                userDAO.getById(checkLoggedIn(ctx).getId()),
                Notifications.NOT_FOUND_ID.getDisplayName()
        );

        int id = getPathId(ctx);

        Message message = TryCatchService.tryEntity(
                messageDAO.getById(id),
                MessageService.buildMessage(
                        Notifications.NOT_FOUND_ID,
                        "Message",
                        String.valueOf(id)
                )
        );


        boolean isOwner = message.getSender().getId() == user.getId();
        boolean isChef = user.getRoles().contains(Role.CHEF);

        if (!isOwner && !isChef) {
            ctx.status(403);
            return;
        }

        messageDAO.deleteById(message.getId());

        ctx.json("Message deleted");
    }

    // ________________________________________________________

    private void getConversation(Context ctx){

        UserDTO user = checkLoggedIn(ctx);

        int other = getPathId(ctx);

        List<MessageDTO> messages = messageDAO
                .getConversation(user.getId(), other)
                .stream()
                .map(MessageDTO::new)
                .toList();

        ctx.json(messages);
    }

    // ________________________________________________________

    private void adminGetConversation(Context ctx) {
        int u1 = TryCatchService.tryParseInt(
                ctx.pathParam("u1"),
                Notifications.MUST_BE_INT.getDisplayName()
        );
        int u2 = TryCatchService.tryParseInt(
                ctx.pathParam("u2"),
                Notifications.MUST_BE_INT.getDisplayName()
        );

        List<MessageDTO> messages = messageDAO
                .getConversation(u1, u2)
                .stream()
                .map(MessageDTO::new)
                .toList();

        ctx.json(messages);
    }

    // ________________________________________________________

    @Override
    protected List<Message> getAllEntities() {
        return messageDAO.getAll();
    }

    // ________________________________________________________

    @Override
    protected Message getEntityById(int id) {
        return messageDAO.getById(id);
    }

}