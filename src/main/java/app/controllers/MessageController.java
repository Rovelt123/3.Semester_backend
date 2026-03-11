package app.controllers;

import app.Main;
import app.controllers.Generic.BaseController;
import app.daos.MessageDAO;
import app.daos.UserDAO;
import app.dtos.MessageDTO;
import app.entities.Message;
import app.entities.User;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;
import java.util.Map;

public class MessageController extends BaseController<Message, MessageDTO> {

    private static final MessageDAO messageDAO = Main.setup.getMessageDAO();
    private static final UserDAO userDAO = Main.setup.getUserDAO();

    public MessageController(){
        super(Message.class, MessageDTO::new);
    }

    public static void registerRoutes(Javalin app){

        MessageController controller = new MessageController();

        app.post("/message", controller::sendMessage);
        app.put("/message/{id}", controller::updateMessage);
        app.delete("/message/{id}", controller::deleteMessage);

        app.get("/messages", controller::getAll);
        app.get("/conversation/{user_id}", controller::getConversation);
    }

    private void sendMessage(Context ctx){

        User sender = ctx.sessionAttribute("user");

        Map<String,String> body = ctx.bodyAsClass(Map.class);

        String content = body.get("content");

        User receiver = ctx.pathParamAsClass("receiver", User.class).get();

        Message message = new Message(sender, receiver, content);

        messageDAO.create(message);

        ctx.status(201).json(new MessageDTO(message));
    }

    private void updateMessage(Context ctx){

        User user = ctx.sessionAttribute("user");

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

    private void deleteMessage(Context ctx){

        User user = ctx.sessionAttribute("user");

        int id = Integer.parseInt(ctx.pathParam("id"));

        Message message = messageDAO.getById(id);

        if(message.getSender().getId() != user.getId()){
            ctx.status(403);
            return;
        }

        messageDAO.delete(message);

        ctx.json("Message deleted");
    }

    private void getConversation(Context ctx){

        User user = ctx.sessionAttribute("user");

        int other = Integer.parseInt(ctx.pathParam("user_id"));

        List<MessageDTO> messages = messageDAO
                .getConversation(user.getId(), other)
                .stream()
                .map(MessageDTO::new)
                .toList();

        ctx.json(messages);
    }

    @Override
    protected List<Message> getAllEntities() {
        return messageDAO.getAll();
    }

    @Override
    protected Message getEntityById(int id) {
        return messageDAO.getById(id);
    }
}