package app.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiException extends RuntimeException {

    private final int code;
    private static final Logger logger = LoggerFactory.getLogger(ApiException.class);

    // ________________________________________________________

    public ApiException(int code, String msg){
        super(msg);
        this.code = code;
        logger.error("ApiException (code={}): {}", code, msg);
    }

    // ________________________________________________________

}