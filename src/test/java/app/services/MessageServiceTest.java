package app.services;

import app.enums.Notifications;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageServiceTest {

    @Test
    void buildMessageOneArg() {
        String message = MessageService.buildMessage(Notifications.TEST_NOTIFICATION_1, "1");
        assertEquals("This is with 1 arg!", message);
    }

    // ________________________________________________________

    @Test
    void buildMessageTwoArgs() {
        String message = MessageService.buildMessage(Notifications.TEST_NOTIFICATION_2, "1", "Hello");
        assertEquals("This is with 1 args! This is the other Hello arg!", message);
    }

    // ________________________________________________________

    @Test
    void sendNotifications() {
        String message = MessageService.buildMessage(Notifications.MUST_BE_INT, "HELLO");
        MessageService.sendError(message);
        MessageService.success(message);
        MessageService.notify(message);
        MessageService.warn(message);
    }
}