package app.exceptions;

/**
 * Purpose: To handle No authorized exceptions in the API
 * Author: Thomas Hartmann
 */
public class NotAuthorizedException extends Exception {
    private final int statusCode;

    public NotAuthorizedException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    // ________________________________________________________

    public NotAuthorizedException(int statusCode, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    // ________________________________________________________

    public int getStatusCode() {
        return statusCode;
    }
}
