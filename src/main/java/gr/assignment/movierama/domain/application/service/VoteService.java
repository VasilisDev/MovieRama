package gr.assignment.movierama.domain.application.service;

import gr.assignment.movierama.core.model.exception.ApplicationException;
import gr.assignment.movierama.core.model.exception.EntityNotFoundException;
import gr.assignment.movierama.domain.model.movie.Movie;
import gr.assignment.movierama.domain.application.service.commands.AddVoteCommand;

public interface VoteService {

    Movie vote(Long authenticatedUserId, AddVoteCommand addVoteCommand) throws ApplicationException;

    Movie retract (Long authenticatedId, Long movieId) throws EntityNotFoundException;
}


