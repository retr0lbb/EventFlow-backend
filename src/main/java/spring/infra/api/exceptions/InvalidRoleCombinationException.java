package spring.infra.api.exceptions;

public class InvalidRoleCombinationException extends RuntimeException {
    public InvalidRoleCombinationException(String message) {
        super(message);
    }
}
