package gr.assignment.movierama.core.model.exception.common;

import gr.assignment.movierama.core.model.exception.ApplicationException;
import gr.assignment.movierama.core.web.IErrorCodeProvider;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum BadRequestExceptionAssert implements ApplicationExceptionAssert {
    VOTE_OWN_MOVIE(400202, BAD_REQUEST, "You cannot vote your own movie."),
    ALREADY_LIKE_MOVIE(400203, BAD_REQUEST, "You already like this movie."),
    ALREADY_DISLIKE_MOVIE(400204, BAD_REQUEST, "You already dislike this movie."),
    INVALID_PASSWORD(400205,BAD_REQUEST,"Invalid password.");

    private final int code;

    private final HttpStatus status;

    private final String message;

    @Override
    public ApplicationException doThrowApplicationException(IErrorCodeProvider errorCodeProvider, Map<String, Object> data) {
        return new ApplicationException(errorCodeProvider, data);
    }

    @Override
    public ApplicationException doThrowApplicationException(IErrorCodeProvider errorCodeProvider, Throwable throwable, Map<String, Object> data) {
        return new ApplicationException(errorCodeProvider, throwable, data);
    }
}