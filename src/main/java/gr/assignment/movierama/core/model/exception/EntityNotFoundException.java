package gr.assignment.movierama.core.model.exception;


import gr.assignment.movierama.core.web.IErrorCodeProvider;

import java.util.Map;

import static gr.assignment.movierama.core.web.GenericErrorCodeProvider.REQUEST_RESOURCE_NOT_FOUND;

public class EntityNotFoundException extends ApplicationException {

    public EntityNotFoundException() {
        super(REQUEST_RESOURCE_NOT_FOUND);
    }

    public EntityNotFoundException(IErrorCodeProvider error, Map<String, Object> data) {
        super(error, data);
    }

    public EntityNotFoundException(IErrorCodeProvider error, Throwable cause, Map<String, Object> data) {
        super(error, cause, data);
    }
}
