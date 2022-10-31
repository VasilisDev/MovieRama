package gr.assignment.movierama.core.model.exception.common;

import gr.assignment.movierama.core.model.exception.ApplicationException;
import gr.assignment.movierama.core.model.exception.EntityNotFoundException;
import gr.assignment.movierama.core.web.IErrorCodeProvider;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ResourceNotFoundExceptionAssert implements ApplicationExceptionAssert {
    VOTE(404100,NOT_FOUND,"No vote was found."),
    MOVIE(404101,NOT_FOUND,"No movie was found."),
    ACCOUNT(404102,NOT_FOUND,"No account was found.");

    private final int code;

    private final HttpStatus status;

    private final String message;

    @Override
    public ApplicationException doThrowApplicationException(IErrorCodeProvider errorCodeProvider, Map<String, Object> data) {
        return new EntityNotFoundException(errorCodeProvider,data);
    }

    @Override
    public ApplicationException doThrowApplicationException(IErrorCodeProvider errorCodeProvider, Throwable throwable, Map<String, Object> data) {
        return new EntityNotFoundException(errorCodeProvider,throwable,data);
    }
}