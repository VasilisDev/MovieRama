package gr.assignment.movierama.core.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@ToString
@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public enum GenericErrorCodeProvider implements IErrorCodeProvider {

    SERVER_ERROR(5000, HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong please contact administrator"),
    REQUEST_VALIDATION_FAILED(5400, HttpStatus.BAD_REQUEST, "Request data format validation failed"),
    REQUEST_RESOURCE_NOT_FOUND(5404, HttpStatus.NOT_FOUND, "Resource not found");

    private final int code;
    private final HttpStatus status;
    private final String message;
}