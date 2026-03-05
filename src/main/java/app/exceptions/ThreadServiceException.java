package app.exceptions;

public class ThreadServiceException extends RuntimeException {

  public ThreadServiceException(String message) {
    super(message);
  }

  // ________________________________________________________

  public ThreadServiceException(String message, Throwable cause) {
    super(message, cause);
  }
}
