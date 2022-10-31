package gr.assignment.movierama.domain.application.service.impl;

import gr.assignment.movierama.core.model.exception.ApplicationException;
import gr.assignment.movierama.core.model.exception.EntityNotFoundException;
import gr.assignment.movierama.core.model.exception.common.BadRequestExceptionAssert;
import gr.assignment.movierama.core.model.exception.common.ResourceNotFoundExceptionAssert;
import gr.assignment.movierama.domain.model.account.Account;
import gr.assignment.movierama.infrastructure.repositories.AccountRepository;
import gr.assignment.movierama.domain.model.movie.Movie;
import gr.assignment.movierama.infrastructure.repositories.MovieRepository;
import gr.assignment.movierama.domain.application.service.VoteService;
import gr.assignment.movierama.domain.application.service.commands.AddVoteCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.Set;

@RequiredArgsConstructor
@Service
@Transactional
public class VoteServiceImpl implements VoteService {

    private final AccountRepository accountRepository;

    private final MovieRepository movieRepository;

    @Override
    public Movie vote(Long authenticatedAccountId, AddVoteCommand addVoteCommand) throws ApplicationException {
        Assert.notNull(authenticatedAccountId, "`authenticatedAccountId` is required in order to proceed the action.");
        Assert.notNull(addVoteCommand, "`addVoteCommand` is required in order to proceed the action.");

        long movieId = addVoteCommand.getMovieId();
        Movie movie = getMovieOrThrow(movieId);

        Account authenticatedAccount = getAccountOrThrow(authenticatedAccountId);

        Account publisher = movie.getAccount();
        BadRequestExceptionAssert.VOTE_OWN_MOVIE.isTrue(!publisher.equals(authenticatedAccount));

        Set<Account> likes = movie.getLikes();
        Set<Account> dislikes = movie.getDislikes();

        boolean isLike = addVoteCommand.isLike();
        boolean isDislike = !isLike;
        boolean hasAlreadyLike = likes.contains(authenticatedAccount) && isLike;
        boolean hasAlreadyDislike = dislikes.contains(authenticatedAccount) && isDislike;

        BadRequestExceptionAssert.ALREADY_LIKE_MOVIE.isTrue(!hasAlreadyLike);
        BadRequestExceptionAssert.ALREADY_DISLIKE_MOVIE.isTrue(!hasAlreadyDislike);

        if (dislikes.contains(authenticatedAccount) && isLike) {
            dislikes.remove(authenticatedAccount);
            likes.add(authenticatedAccount);
        } else if (likes.contains(authenticatedAccount) && isDislike) {
            likes.remove(authenticatedAccount);
            dislikes.add(authenticatedAccount);
        }
        else {
            if (isLike) {
                likes.add(authenticatedAccount);
            } else {
                dislikes.add(authenticatedAccount);
            }
        }
       return movieRepository.save(movie);
    }

    @Override
    public Movie retract(Long authenticatedAccountId, Long movieId) throws EntityNotFoundException {
        Assert.notNull(authenticatedAccountId, "`authenticatedAccountId` is required in order to proceed the action.");
        Assert.notNull(movieId, "`movieId` is required in order to proceed the action.");

        Movie movie = getMovieOrThrow(movieId);
        Account authenticatedAccount = getAccountOrThrow(authenticatedAccountId);

        Set<Account> likes = movie.getLikes();
        Set<Account> dislikes = movie.getDislikes();

        if(likes.contains(authenticatedAccount)) {
            likes.remove(authenticatedAccount);
        } else if(dislikes.contains(authenticatedAccount)) {
            dislikes.remove(authenticatedAccount);
        } else {
            throwVoteNotFoundException(authenticatedAccountId);
        }
        return movieRepository.save(movie);
    }

    private void throwVoteNotFoundException(Long authenticatedAccountId) {
        throw new EntityNotFoundException(ResourceNotFoundExceptionAssert.VOTE, Collections.singletonMap("accountId", authenticatedAccountId));
    }

    private Account getAccountOrThrow(Long authenticatedAccountId) {
        return accountRepository.findById(authenticatedAccountId).orElseThrow(() -> new EntityNotFoundException(ResourceNotFoundExceptionAssert.ACCOUNT, Collections.singletonMap("accountId", authenticatedAccountId)));
    }

    private Movie getMovieOrThrow(Long movieId) {
        return movieRepository.findById(movieId).orElseThrow(() -> new EntityNotFoundException(ResourceNotFoundExceptionAssert.MOVIE, Collections.singletonMap("movieId", movieId)));
    }
}