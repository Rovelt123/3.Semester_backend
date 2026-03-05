package app.exceptions;

public class ApiException extends RuntimeException {

    private int code;

    // ________________________________________________________

    public ApiException(int code, String msg){
        super(msg);
        this.code = code;
    }

    // ________________________________________________________

    public int getCode(){
        return code;
    }
}