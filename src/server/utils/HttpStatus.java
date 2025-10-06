package server.utils;

public enum HttpStatus {
    OK(200),
    CREATED(201),
    BAD_REQUEST(400),
    NOT_FOUND(404),
    NOT_ALLOWED(405),
    NOT_ACCEPTABLE(406),
    INTERNAL_ERROR(500);

    public final int code;

    HttpStatus(int code) {
        this.code = code;
    }
}
