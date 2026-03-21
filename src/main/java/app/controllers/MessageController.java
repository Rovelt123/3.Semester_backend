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
        User user = TryCatchService.tryEntity(
            userDAO.getById(sender.getId()),
            MessageService.buildMessage(
                Notifications.NOT_FOUND_ID,
                "User",
                String.valueOf(sender.getId())
            )
        );

        Map<String,String> body = TryCatchService.tryBodyMap(ctx, Notifications.BODY_EMPTY.getDisplayName());

        String content = TryCatchService.tryString(
            body.get("content"),
            Notifications.STRING_EMPTY.getDisplayName()
        );

        int id = getPathId(ctx);
        User receiver = TryCatchService.tryEntity(
            userDAO.getById(id),
            MessageService.buildMessage(
                Notifications.NOT_FOUND_ID,
                "User",
                String.valueOf(id)
            )
        );

        Message message = new Message(user, receiver, content);

        messageDAO.create(new Message(user, receiver, content));

        ctx.status(201).json(new MessageDTO(message));
    }

    // ________________________________________________________

    private void updateMessage(Context ctx){

        UserDTO user = checkLoggedIn(ctx);

        int id = getPathId(ctx);

        Message message = TryCatchService.tryEntity(
                messageDAO.getById(id),
                MessageService.buildMessage(
                    Notifications.NOT_FOUND_ID,
                    "Message",
                    String.valueOf(id)
                )
        );

        if(message.getSender().getId() != user.getId()){
            ctx.status(403);
            return;
        }

        Map<String,String> body = TryCatchService.tryBodyMap(
            ctx,
            Notifications.BODY_EMPTY.getDisplayName()
        );

        message.setContent(
            TryCatchService.tryString(
                body.get("content"),
                Notifications.STRING_EMPTY.getDisplayName()
            )
        );

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

        if (message.getSender().getId() != user.getId() || !user.getRoles().contains(Role.CHEF)) {
            ctx.status(403);
            return;
        }

        messageDAO.deleteById(message.getId());

        String messageOutput = MessageService.buildMessage(
            Notifications.DELETED_WITH_ID,
                "Message",
                String.valueOf(id)
        );

        ctx.json(messageOutput);
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