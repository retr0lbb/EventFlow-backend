package spring.infra.api.exceptions;

public class EventAtFullCapacityException extends RuntimeException {
    public EventAtFullCapacityException(String message) {
        super(message);
    }
}
