package gr.assignment.movierama.core.model.exception;


import gr.assignment.movierama.core.web.IErrorCodeProvider;

import java.util.Map;

public class EntityAlreadyExistsException extends ApplicationException {

    public EntityAlreadyExistsException(IErrorCodeProvider error, Map<String, Object> data) {
        super(error, data);
    }

    public EntityAlreadyExistsException(IErrorCodeProvider error, Throwable cause, Map<String, Object> data) {
        super(error, cause, data);
    }
}
