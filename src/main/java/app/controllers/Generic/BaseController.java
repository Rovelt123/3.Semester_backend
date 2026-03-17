package app.controllers.Generic;

import app.enums.Notifications;
import app.services.MessageService;
import app.services.TryCatchService;
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

        if (getAllEntities().isEmpty()) {
            String message = MessageService.buildMessage(Notifications.GET_ALL_EMPTY, entityClass.getSimpleName().toLowerCase(Locale.ROOT));
            ctx.status(200).json(message);
            return;
        }

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
        int id = TryCatchService.tryParseInt(ctx.pathParam("id"), MessageService.buildMessage(Notifications.MUST_BE_INT, ctx.pathParam("id")));

        T entity = TryCatchService.tryEntity(
            getEntityById(id),
            MessageService.buildMessage(
                Notifications.NOT_FOUND_ID,
                entityClass.getSimpleName().substring(0,1).toUpperCase() + entityClass.getSimpleName().substring(1).toLowerCase(),
                String.valueOf(id)
            )
        );

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
    }
}