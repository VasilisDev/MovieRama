package gr.assignment.movierama.core.web;


import gr.assignment.movierama.core.model.exception.ApplicationException;
import gr.assignment.movierama.core.model.exception.EntityAlreadyExistsException;
import gr.assignment.movierama.core.model.exception.EntityNotFoundException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestControllerAdvice
public class RestErrorHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<?> handleAppException(ApplicationException ex, HttpServletRequest request) {
        RestErrorResponse representation = new RestErrorResponse(ex, request.getRequestURI());
        return new ResponseEntity<>(representation, new HttpHeaders(), ex.getError().getStatus());
    }

    @ExceptionHandler({EntityNotFoundException.class, EntityAlreadyExistsException.class})
    public ResponseEntity<?> handleEntityException(ApplicationException ex, HttpServletRequest request) {
        RestErrorResponse errorResponse = new RestErrorResponse(ex, request.getRequestURI());
        return ResponseEntity.status(ex.getError().getStatus()).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<?> handleException(Exception e, HttpServletRequest httpServletRequest) {
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(
                new RestErrorResponse(new ApplicationException(GenericErrorCodeProvider.SERVER_ERROR), httpServletRequest.getRequestURI()));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        final Map<String, List<String>> result = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(it -> (FieldError) it)
                .collect(Collectors.groupingBy(FieldError::getField,
                        Collectors.mapping(DefaultMessageSourceResolvable::getDefaultMessage, Collectors.toList())));

        String URI = ((ServletWebRequest) request).getRequest().getRequestURI();
        return ResponseEntity.badRequest().body(
                new RestErrorResponse(GenericErrorCodeProvider.REQUEST_VALIDATION_FAILED.getCode(), BAD_REQUEST.value(),
                        GenericErrorCodeProvider.REQUEST_VALIDATION_FAILED.getMessage(), URI, result));
    }
}