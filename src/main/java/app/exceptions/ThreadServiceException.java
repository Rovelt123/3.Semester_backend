package app.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadServiceException extends RuntimeException {

  private static final Logger logger = LoggerFactory.getLogger(ThreadServiceException.class);

  // ________________________________________________________

  public ThreadServiceException(String message, Throwable cause) {
    super(message, cause);
    logger.error("ApiException (code={}): {}", cause, message);
  }
}
