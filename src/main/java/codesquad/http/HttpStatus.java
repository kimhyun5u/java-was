package codesquad.http;

public enum HttpStatus {
    OK(200, "OK"), NOT_FOUND(404, "Not Found"), CREATED(201, "Created");
    private final int code;
    private final String message;

    HttpStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static String getStatusMessage(int code) {
        for (HttpStatus status : HttpStatus.values()) {
            if (status.code == code) {
                return status.message;
            }
        }
        return NOT_FOUND.message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
