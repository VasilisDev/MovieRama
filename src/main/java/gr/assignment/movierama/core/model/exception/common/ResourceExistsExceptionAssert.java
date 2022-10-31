package gr.assignment.movierama.core.model.exception.common;

import gr.assignment.movierama.core.model.exception.ApplicationException;
import gr.assignment.movierama.core.model.exception.EntityAlreadyExistsException;
import gr.assignment.movierama.core.web.IErrorCodeProvider;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ResourceExistsExceptionAssert implements ApplicationExceptionAssert {
    USERNAME(400101, BAD_REQUEST, "Username already exists!"),
    MOVIE(400102, BAD_REQUEST, "Movie already exists!");

    private final int code;
    private final HttpStatus status;
    private final String message;

    @Override
    public ApplicationException doThrowApplicationException(IErrorCodeProvider errorCodeProvider, Map<String, Object> data) {
        return new EntityAlreadyExistsException(errorCodeProvider,data);
    }

    @Override
    public ApplicationException doThrowApplicationException(IErrorCodeProvider errorCodeProvider, Throwable throwable, Map<String, Object> data) {
        return new EntityAlreadyExistsException(errorCodeProvider,throwable,data);
    }
}