package app.services;

import app.Main;
import app.enums.Notifications;
import app.exceptions.ApiException;
import app.utils.ErrorHandler;
import app.utils.Utils;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class VersionService {

    private static final MessageService messageService = Main.setup.getMessageService();
    private static final String versionUrl =
            "https://raw.githubusercontent.com/Rovelt123/3.Semester_backend/master/src/main/resources/config.properties";

    // ________________________________________________________

    private static double getRemoteVersion() {
        try {
            URL url = new URL(versionUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int response = connection.getResponseCode();

            if (response != HttpURLConnection.HTTP_OK) {
                throw new Exception("HTTP request failed" + response);
            }

            try (Scanner scan = new Scanner(connection.getInputStream())) {

                while (scan.hasNextLine()) {
                    String line = scan.nextLine().trim();

                    if (line.startsWith("VERSION =")) {
                        String version = line.split("=")[1].trim();
                        return Double.parseDouble(version);
                    }
                }
            }

        } catch (Exception e) {
            throw new ApiException(500, "Error fetching version: " + " | Dev msg: " +e.getMessage());
        }
        return 0.0;
    }

    // ________________________________________________________

    private static double getLocalVersion() {

        String version = Utils.getPropertyValue("VERSION", "config.properties");

        return ErrorHandler.tryParseDouble(
            version,
            "Could not parse double!"
        );
    }

    // ________________________________________________________

    public static String checkVersion() {

        double local = getLocalVersion();
        double remote = getRemoteVersion();

        if (remote > local) {
            return messageService.buildMessage(
                Notifications.NEW_UPDATE,
                String.valueOf(remote),
                String.valueOf(local)
            );
        } else {
            return Notifications.UP_TO_DATE.getDisplayName();
        }
    }
}