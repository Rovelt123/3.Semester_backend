package app.controllers.Generic;

import app.enums.Notifications;
import app.services.MessageService;
import io.javalin.http.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public abstract class BaseController<T, DTO> implements IController {

    protected Class<T> entityClass;
    protected EntityMapper<T, DTO> mapper;
    protected abstract List<T> getAllEntities();
    protected abstract T getEntityById(int id);

    // ________________________________________________________

    protected BaseController(Class<T> entityClass, EntityMapper<T, DTO> mapper) {
        this.entityClass = entityClass;
        this.mapper = mapper;
    }

    // ________________________________________________________

    @Override
    public void getAll(Context ctx) {

        List<DTO> list = new ArrayList<>();

        getAllEntities().forEach(entity -> {
            list.add(mapper.map(entity));
        });

        String message = MessageService.buildMessage(
                Notifications.GET_ALL,
                String.valueOf(list.size()),
                entityClass.getSimpleName().toLowerCase(Locale.ROOT)
        );

        MessageService.notify(message);

        ctx.status(200).json(Map.of(
                "Data", list,
                "Message", message
        ));
    }

    // ________________________________________________________

    @Override
    public void getByID(Context ctx) {

        try {

            int id = Integer.parseInt(ctx.pathParam("id"));

            T entity = getEntityById(id);

            if (entity == null) {
                ctx.status(404);
                return;
            }

            DTO dto = mapper.map(entity);

            String message = MessageService.buildMessage(
                    Notifications.GET_BY_ID,
                    entityClass.getSimpleName().toLowerCase(Locale.ROOT),
                    String.valueOf(id)
            );

            MessageService.notify(message);

            ctx.status(200).json(Map.of(
                    "Data", dto,
                    "Message", message
            ));

        } catch (NumberFormatException e) {

            String message = MessageService.buildMessage(
                    Notifications.MUST_BE_INT,
                    ctx.pathParam("id")
            );

            MessageService.sendError(message);

            ctx.status(400).json(message);
        }
    }
}