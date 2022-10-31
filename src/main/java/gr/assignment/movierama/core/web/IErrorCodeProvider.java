package gr.assignment.movierama.core.web;

import org.springframework.http.HttpStatus;

public interface IErrorCodeProvider {
    int getCode();
    String getMessage();
    HttpStatus getStatus();
}
