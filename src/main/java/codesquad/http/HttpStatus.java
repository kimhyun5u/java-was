package codesquad.http;

public enum HttpStatus {
    OK(200, "OK"), NOT_FOUND(404, "Not Found"), CREATED(201, "Created"), REDIRECT_MOVE_PERMANENTLY(301, "Move Permanently"), BAD_REQUEST(400, "Bad Request"),
    ;
    private final int code;
    private final String message;

    HttpStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
