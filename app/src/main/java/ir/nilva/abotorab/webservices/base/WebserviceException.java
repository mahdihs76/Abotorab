package ir.nilva.abotorab.webservices.base;

public class WebserviceException extends Exception {
    private int code;
    private String message;

    public WebserviceException(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String
    toString() {
        return "WebserviceException{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
