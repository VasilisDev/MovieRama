package gr.assignment.movierama.domain.application.service;

import gr.assignment.movierama.core.model.exception.EntityAlreadyExistsException;
import gr.assignment.movierama.core.model.exception.EntityNotFoundException;
import gr.assignment.movierama.domain.model.movie.Movie;
import gr.assignment.movierama.domain.application.service.commands.AddMovieCommand;

import java.util.List;
import java.util.Optional;

public interface MovieService {

    List<Movie> findAllBy(Optional<String> username);

    Movie create(AddMovieCommand addMovieCommand) throws EntityAlreadyExistsException, EntityNotFoundException;
}
