package spring.infra.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class CnpjAlreadyExistsException extends RuntimeException {
    public CnpjAlreadyExistsException(String message) {
        super(message);
    }
}
