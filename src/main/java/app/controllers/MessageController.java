package app.controllers;

import app.Main;
import app.controllers.Generic.BaseController;
import app.daos.MessageDAO;
import app.dtos.MessageDTO;
import app.entities.Message;
import io.javalin.Javalin;

import java.util.List;

public class MessageController extends BaseController<Message, MessageDTO> {

    private static final MessageDAO messageDAO = Main.setup.getMessageDAO();

    // ________________________________________________________

    public MessageController(){
        super(Message.class, MessageDTO::new);
    }

    // ________________________________________________________

    public static void registerRoutes(Javalin app) {
        MessageController controller = new MessageController();

        //GET
        app.get("/messages", controller::getAll);
        app.get("/message/id", controller::getByID);

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
