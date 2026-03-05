package app.services;

import app.enums.Notifications;

public class MessageService {
    /*

    This is made for future purposes - Notify system
    This is supposed to be a generic notify system with different color codes ind the future.

     */

    public static String buildMessage(Notifications notifications, String... args) {
        return String.format(notifications.getDisplayName(), args);
    }

    // ________________________________________________________

    public static void sendError(String message){
        System.out.println("ERROR: " + message);
    }

    // ________________________________________________________

    public static void succes(String message) {
        System.out.println("SUCESS: " + message);
    }

    // ________________________________________________________

    public static void notify(String message) {
        System.out.println(message);
    }

    // ________________________________________________________

    public static void warn(String message) {
        System.out.println("WARNING: " +message);
    }
}
