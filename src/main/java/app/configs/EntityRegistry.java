package app.configs;

import app.entities.*;
import org.hibernate.cfg.Configuration;

final class EntityRegistry {

    private EntityRegistry() {}

    static void registerEntities(Configuration configuration) {
        configuration.addAnnotatedClass(Announcement.class);
        configuration.addAnnotatedClass(Holiday.class);
        //configuration.addAnnotatedClass(Message.class);
        configuration.addAnnotatedClass(Responsibility.class);
        configuration.addAnnotatedClass(Shift.class);
        configuration.addAnnotatedClass(ShiftRequest.class);
        configuration.addAnnotatedClass(User.class);
        configuration.addAnnotatedClass(Response.class);
        // TODO: Add more entities here...
    }
}