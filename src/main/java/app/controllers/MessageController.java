package app.controllers;

import app.Main;
import app.controllers.Generic.BaseController;
import app.daos.MessageDAO;
import app.daos.UserDAO;
import app.dtos.MessageDTO;
import app.dtos.UserDTO;
import app.entities.Message;
import app.entities.User;
import app.enums.Role;
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

            get("/messages", controller::getAll, Role.USER);
            get("/conversation/{user_id}", controller::getConversation, Role.USER);
        };
    }

    // ________________________________________________________

    private void sendMessage(Context ctx){

        UserDTO sender = ctx.attribute("user");
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

        UserDTO user = ctx.attribute("user");

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

        UserDTO user = ctx.attribute("user");

        int id = Integer.parseInt(ctx.pathParam("id"));

        Message message = messageDAO.getById(id);

        if(message.getSender().getId() != user.getId()){
            ctx.status(403);
            return;
        }

        messageDAO.deleteById(message.getId());

        ctx.json("Message deleted");
    }

    // ________________________________________________________

    private void getConversation(Context ctx){

        UserDTO user = ctx.attribute("user");

        int other = Integer.parseInt(ctx.pathParam("user_id"));

        List<MessageDTO> messages = messageDAO
                .getConversation(user.getId(), other)
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