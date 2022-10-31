package gr.assignment.movierama.domain.application.service.impl;

import gr.assignment.movierama.core.model.exception.EntityAlreadyExistsException;
import gr.assignment.movierama.core.model.exception.EntityNotFoundException;
import gr.assignment.movierama.core.model.exception.common.ResourceExistsExceptionAssert;
import gr.assignment.movierama.core.model.exception.common.ResourceNotFoundExceptionAssert;
import gr.assignment.movierama.domain.model.account.Account;
import gr.assignment.movierama.infrastructure.repositories.AccountRepository;
import gr.assignment.movierama.domain.model.movie.Movie;
import gr.assignment.movierama.infrastructure.repositories.MovieRepository;
import gr.assignment.movierama.domain.application.service.MovieService;
import gr.assignment.movierama.domain.application.service.commands.AddMovieCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;

    private final AccountRepository accountRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Movie> findAllBy(Optional<String> username) {
        return username.map(movieRepository::findMoviesByAccount_UsernameValue).orElseGet(movieRepository::findAll);
    }

    @Override
    public Movie create(AddMovieCommand addMovieCommand) throws EntityAlreadyExistsException, EntityNotFoundException {
        Assert.notNull(addMovieCommand, "`addMovieCommand` is required in order to proceed the action.");

        String movieTitle = addMovieCommand.getTitle();
        movieRepository.findByTitle(movieTitle).ifPresent(m -> {
            throw new EntityAlreadyExistsException(ResourceExistsExceptionAssert.MOVIE, Collections.singletonMap("title", movieTitle));
        });

        final long accountId = addMovieCommand.getAccountId();
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() ->
                        new EntityNotFoundException(ResourceNotFoundExceptionAssert.ACCOUNT, Collections.singletonMap("accountId", accountId)));

        return movieRepository.save(Movie.create(account, movieTitle, addMovieCommand.getDescription()));
    }
}