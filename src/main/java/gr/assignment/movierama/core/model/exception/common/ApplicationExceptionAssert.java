package gr.assignment.movierama.core.model.exception.common;



import gr.assignment.movierama.core.model.exception.ApplicationException;
import gr.assignment.movierama.core.web.IErrorCodeProvider;

import java.text.MessageFormat;
import java.util.Map;

public interface ApplicationExceptionAssert extends IErrorCodeProvider, Assert {

    @Override
    default ApplicationException newException() {
        return doThrowApplicationException(this, null);
    }

    @Override
    default ApplicationException newException(Map<String, Object> data) {
        return doThrowApplicationException(this, data);
    }

    @Override
    default ApplicationException newException(Throwable t, Map<String, Object> data) {
        return doThrowApplicationException(this, t, data);
    }

    default String formatMessage(Object... args) {
        return MessageFormat.format(this.getMessage(), args);
    }

    ApplicationException doThrowApplicationException(IErrorCodeProvider errorCodeProvider, Map<String, Object> data);

    ApplicationException doThrowApplicationException(IErrorCodeProvider errorCodeProvider, Throwable throwable, Map<String, Object> data);
}