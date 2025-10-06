package exceptions;

public class NotAcceptedTaskException extends RuntimeException {
    public NotAcceptedTaskException(String message) {
        super(message);
    }
}
