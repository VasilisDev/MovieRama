package gr.assignment.movierama.domain.application.service.impl;

import gr.assignment.movierama.core.model.exception.ApplicationException;
import gr.assignment.movierama.core.model.exception.EntityNotFoundException;
import gr.assignment.movierama.core.model.exception.common.BadRequestExceptionAssert;
import gr.assignment.movierama.core.model.exception.common.ResourceNotFoundExceptionAssert;
import gr.assignment.movierama.domain.application.service.commands.AddVoteCommand;
import gr.assignment.movierama.domain.model.account.Account;
import gr.assignment.movierama.domain.model.account.AccountProfile;
import gr.assignment.movierama.infrastructure.repositories.AccountRepository;
import gr.assignment.movierama.domain.model.account.Username;
import gr.assignment.movierama.domain.model.movie.Movie;
import gr.assignment.movierama.infrastructure.repositories.MovieRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

public class VoteServiceImplTests {

    private AccountRepository accountRepository;

    private MovieRepository movieRepository;

    private VoteServiceImpl voteService;

    private Account user1;

    private Account user2;

    @Before
    public void beforeSetup() {
        this.user1 = new Account(Username.of("username_1"), "password", AccountProfile.of("firstname_1", "lastname_1"));
        this.user2 = new Account(Username.of("username_2"), "password", AccountProfile.of("firstname_2", "lastname_2"));
        this.accountRepository = mock(AccountRepository.class);
        this.movieRepository = mock(MovieRepository.class);
        this.voteService = new VoteServiceImpl(this.accountRepository, this.movieRepository);
    }

    @Test
    public void vote_nullCommand_shouldThrowException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            this.voteService.vote(0L, null);
        });

        assertEquals(ex.getMessage(), "`addVoteCommand` is required in order to proceed the action.");
    }

    @Test
    public void vote_nullAuthenticatedAccountId_shouldThrowException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            this.voteService.vote(null, new AddVoteCommand(0L, 0L, false));
        });

        assertEquals(ex.getMessage(), "`authenticatedAccountId` is required in order to proceed the action.");
    }

    @Test
    public void vote_movieNotExists_shouldThrowException() {
        AddVoteCommand addVoteCommand = new AddVoteCommand(1L, 1L, false);
        when(this.movieRepository.findById(addVoteCommand.getMovieId())).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> {
            this.voteService.vote(1L, addVoteCommand);
        });

        assertEquals(ex.getMessage(), "No movie was found.");
        assertEquals(ex.getError(), ResourceNotFoundExceptionAssert.MOVIE);

        verify(this.accountRepository, never()).findById(any());
        verify(this.movieRepository, never()).save(any());
        verify(this.movieRepository, times(1)).findById(addVoteCommand.getMovieId());
    }

    @Test
    public void vote_authenticatedUserNotFound_shouldThrowException() {
        long movieId = 1L;
        long authenticatedUserId = 1L;

        AddVoteCommand addVoteCommand = new AddVoteCommand(authenticatedUserId, movieId, false);

        Movie movie = new Movie();
        movie.setId(movieId);
        movie.setAccount(user1);
        movie.setDescription("description");
        movie.setTitle("title");

        when(this.movieRepository.findById(addVoteCommand.getMovieId())).thenReturn(Optional.of(movie));
        when(this.accountRepository.findById(authenticatedUserId)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> {
            this.voteService.vote(authenticatedUserId, addVoteCommand);
        });

        assertEquals(ex.getMessage(), "No account was found.");
        assertEquals(ex.getError(), ResourceNotFoundExceptionAssert.ACCOUNT);

        InOrder inOrder = inOrder(this.accountRepository, this.movieRepository);
        inOrder.verify(this.movieRepository, times(1)).findById(addVoteCommand.getMovieId());
        inOrder.verify(this.accountRepository, times(1)).findById(authenticatedUserId);
        verify(this.movieRepository, never()).save(any());
    }

    @Test
    public void vote_authenticatedUserVoteOwnMovie_shouldThrowException() {
        long movieId = 1L;
        long authenticatedUserId = 1L;

        AddVoteCommand addVoteCommand = new AddVoteCommand(authenticatedUserId, movieId, false);

        Movie movie = new Movie();
        movie.setId(movieId);
        movie.setAccount(user1);
        movie.setDescription("description");
        movie.setTitle("title");

        when(this.movieRepository.findById(addVoteCommand.getMovieId())).thenReturn(Optional.of(movie));
        when(this.accountRepository.findById(authenticatedUserId)).thenReturn(Optional.of(user1));

        ApplicationException ex = assertThrows(ApplicationException.class, () -> {
            this.voteService.vote(authenticatedUserId, addVoteCommand);
        });

        assertEquals(ex.getMessage(), "You cannot vote your own movie.");
        assertEquals(ex.getError(), BadRequestExceptionAssert.VOTE_OWN_MOVIE);

        InOrder inOrder = inOrder(this.accountRepository, this.movieRepository);
        inOrder.verify(this.movieRepository, times(1)).findById(addVoteCommand.getMovieId());
        inOrder.verify(this.accountRepository, times(1)).findById(authenticatedUserId);
        verify(this.movieRepository, never()).save(any());
    }

    @Test
    public void vote_alreadyLikeMovie_shouldThrowException() {
        long movieId = 1L;
        long authenticatedUserId = 1L;

        AddVoteCommand addVoteCommand = new AddVoteCommand(authenticatedUserId, movieId, true);

        Movie movie = new Movie();
        movie.setId(movieId);
        movie.setAccount(user1);
        movie.setDescription("description");
        movie.setTitle("title");
        movie.getLikes().add(user2);

        when(this.movieRepository.findById(addVoteCommand.getMovieId())).thenReturn(Optional.of(movie));
        when(this.accountRepository.findById(authenticatedUserId)).thenReturn(Optional.of(user2));

        ApplicationException ex = assertThrows(ApplicationException.class, () -> {
            this.voteService.vote(authenticatedUserId, addVoteCommand);
        });

        assertEquals(ex.getMessage(), "You already like this movie.");
        assertEquals(ex.getError(), BadRequestExceptionAssert.ALREADY_LIKE_MOVIE);

        InOrder inOrder = inOrder(this.accountRepository, this.movieRepository);
        inOrder.verify(this.movieRepository, times(1)).findById(movieId);
        inOrder.verify(this.accountRepository, times(1)).findById(authenticatedUserId);
        verify(this.movieRepository, never()).save(any());
    }

    @Test
    public void vote_alreadyDislikeMovie_shouldThrowException() {
        long movieId = 1L;
        long authenticatedUserId = 1L;

        AddVoteCommand addVoteCommand = new AddVoteCommand(authenticatedUserId, movieId, false);

        Movie movie = new Movie();
        movie.setId(movieId);
        movie.setAccount(user1);
        movie.setDescription("description");
        movie.setTitle("title");
        movie.getDislikes().add(user2);

        when(this.movieRepository.findById(addVoteCommand.getMovieId())).thenReturn(Optional.of(movie));
        when(this.accountRepository.findById(authenticatedUserId)).thenReturn(Optional.of(user2));

        ApplicationException ex = assertThrows(ApplicationException.class, () -> {
            this.voteService.vote(authenticatedUserId, addVoteCommand);
        });

        assertEquals(ex.getMessage(), "You already dislike this movie.");
        assertEquals(ex.getError(), BadRequestExceptionAssert.ALREADY_DISLIKE_MOVIE);

        InOrder inOrder = inOrder(this.accountRepository, this.movieRepository);
        inOrder.verify(this.movieRepository, times(1)).findById(movieId);
        inOrder.verify(this.accountRepository, times(1)).findById(authenticatedUserId);
        verify(this.movieRepository, never()).save(any());
    }

    @Test
    public void vote_likeMovie_shouldPass() {
        long movieId = 1L;
        long authenticatedUserId = 1L;

        AddVoteCommand addVoteCommand = new AddVoteCommand(authenticatedUserId, movieId, true);

        Movie movie = new Movie();
        movie.setId(movieId);
        movie.setAccount(user1);
        final String description = "description";
        movie.setDescription(description);
        final String title = "title";
        movie.setTitle(title);
        movie.getDislikes().add(user2);

        Movie expectedMovie = new Movie();
        expectedMovie.setId(movieId);
        expectedMovie.setAccount(user1);
        expectedMovie.setDescription(description);
        expectedMovie.setTitle(title);
        expectedMovie.getLikes().add(user2);

        when(this.movieRepository.findById(addVoteCommand.getMovieId())).thenReturn(Optional.of(movie));
        when(this.accountRepository.findById(authenticatedUserId)).thenReturn(Optional.of(user2));
        when(this.movieRepository.save(any(Movie.class))).thenReturn(movie);

        Movie actualMovie =  this.voteService.vote(authenticatedUserId, addVoteCommand);

        assertEquals(expectedMovie, actualMovie);

        InOrder inOrder = inOrder(this.accountRepository, this.movieRepository);
        inOrder.verify(this.movieRepository, times(1)).findById(movieId);
        inOrder.verify(this.accountRepository, times(1)).findById(authenticatedUserId);
        inOrder.verify(this.movieRepository, times(1)).save(any(Movie.class));
    }

    @Test
    public void vote_dislikeMovie_shouldPass() {
        long movieId = 1L;
        long authenticatedUserId = 1L;

        AddVoteCommand addVoteCommand = new AddVoteCommand(authenticatedUserId, movieId, false);

        Movie movie = new Movie();
        movie.setId(movieId);
        movie.setAccount(user1);
        final String description = "description";
        movie.setDescription(description);
        final String title = "title";
        movie.setTitle(title);
        movie.getLikes().add(user2);

        Movie expectedMovie = new Movie();
        expectedMovie.setId(movieId);
        expectedMovie.setAccount(user1);
        expectedMovie.setDescription(description);
        expectedMovie.setTitle(title);
        expectedMovie.getDislikes().add(user2);

        when(this.movieRepository.findById(addVoteCommand.getMovieId())).thenReturn(Optional.of(movie));
        when(this.accountRepository.findById(authenticatedUserId)).thenReturn(Optional.of(user2));
        when(this.movieRepository.save(any(Movie.class))).thenReturn(movie);

        Movie actualMovie =  this.voteService.vote(authenticatedUserId, addVoteCommand);

        assertEquals(expectedMovie, actualMovie);

        InOrder inOrder = inOrder(this.accountRepository, this.movieRepository);
        inOrder.verify(this.movieRepository, times(1)).findById(movieId);
        inOrder.verify(this.accountRepository, times(1)).findById(authenticatedUserId);
        inOrder.verify(this.movieRepository, times(1)).save(any(Movie.class));
    }

    @Test
    public void vote_firstTimeVoteLikeMovie_shouldPass() {
        long movieId = 1L;
        long authenticatedUserId = 1L;

        AddVoteCommand addVoteCommand = new AddVoteCommand(authenticatedUserId, movieId, true);

        Movie movie = new Movie();
        movie.setId(movieId);
        movie.setAccount(user1);
        final String description = "description";
        movie.setDescription(description);
        final String title = "title";
        movie.setTitle(title);

        Movie expectedMovie = new Movie();
        expectedMovie.setId(movieId);
        expectedMovie.setAccount(user1);
        expectedMovie.setDescription(description);
        expectedMovie.setTitle(title);
        expectedMovie.getLikes().add(user2);

        when(this.movieRepository.findById(addVoteCommand.getMovieId())).thenReturn(Optional.of(movie));
        when(this.accountRepository.findById(authenticatedUserId)).thenReturn(Optional.of(user2));
        when(this.movieRepository.save(any(Movie.class))).thenReturn(movie);

        Movie actualMovie =  this.voteService.vote(authenticatedUserId, addVoteCommand);

        assertEquals(expectedMovie, actualMovie);

        InOrder inOrder = inOrder(this.accountRepository, this.movieRepository);
        inOrder.verify(this.movieRepository, times(1)).findById(movieId);
        inOrder.verify(this.accountRepository, times(1)).findById(authenticatedUserId);
        inOrder.verify(this.movieRepository, times(1)).save(any(Movie.class));
    }

    @Test
    public void retract_nullAuthenticatedAccountId_shouldThrowException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            this.voteService.retract(null, 0L);
        });

        assertEquals(ex.getMessage(), "`authenticatedAccountId` is required in order to proceed the action.");
    }

    @Test
    public void vote_nullMovieId_shouldThrowException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            this.voteService.retract(1L, null);
        });

        assertEquals(ex.getMessage(), "`movieId` is required in order to proceed the action.");
    }

    @Test
    public void retract_movieNotFound_shouldThrowException() {
        when(this.movieRepository.findById(any())).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> {
            this.voteService.retract(1L, 1L);
        });

        assertEquals(ex.getMessage(), "No movie was found.");
        assertEquals(ex.getError(), ResourceNotFoundExceptionAssert.MOVIE);

        verify(this.movieRepository, times(1)).findById(any());
        verify(this.accountRepository, never()).findById(any());
        verify(this.movieRepository, never()).save(any());
    }


    @Test
    public void retract_authenticatedUserNotFound_shouldThrowException() {
        when(this.movieRepository.findById(any())).thenReturn(Optional.of(new Movie()));
        when(this.accountRepository.findById(any())).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> {
            this.voteService.retract(1L, 1L);
        });

        assertEquals(ex.getMessage(), "No account was found.");
        assertEquals(ex.getError(), ResourceNotFoundExceptionAssert.ACCOUNT);

        InOrder inOrder = inOrder(this.accountRepository, this.movieRepository);
        inOrder.verify(this.movieRepository, times(1)).findById(any());
        inOrder.verify(this.accountRepository, times(1)).findById(any());
        verify(this.movieRepository, never()).save(any());
    }

    @Test
    public void retract_voteNotFound_shouldThrowException() {
        when(this.movieRepository.findById(any())).thenReturn(Optional.of(new Movie()));
        when(this.accountRepository.findById(any())).thenReturn(Optional.of(user1));

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> {
            this.voteService.retract(1L, 1L);
        });

        assertEquals(ex.getMessage(), "No vote was found.");
        assertEquals(ex.getError(), ResourceNotFoundExceptionAssert.VOTE);

        InOrder inOrder = inOrder(this.accountRepository, this.movieRepository);
        inOrder.verify(this.movieRepository, times(1)).findById(any());
        inOrder.verify(this.accountRepository, times(1)).findById(any());
        verify(this.movieRepository, never()).save(any());
    }

    @Test
    public void retract_likes_shouldPass() {
        final String title = "title";
        final String description = "description";

        Movie movie = new Movie();
        movie.setId(1L);
        movie.setAccount(user1);
        movie.setDescription(description);
        movie.setTitle(title);
        movie.getLikes().add(user2);

        //movie without likes and dislikes, other properties doesn't matter
        Movie expectedResult = new Movie();
        expectedResult.setId(1L);
        expectedResult.setAccount(user1);
        expectedResult.setDescription(description);
        expectedResult.setTitle(title);

        when(this.movieRepository.findById(any())).thenReturn(Optional.of(movie));
        when(this.accountRepository.findById(any())).thenReturn(Optional.of(user2));
        when(this.movieRepository.save(any(Movie.class))).thenReturn(movie);

        Movie actualResult = this.voteService.retract(1L, 1L);

        assertEquals(expectedResult, actualResult);

        InOrder inOrder = inOrder(this.accountRepository, this.movieRepository);
        inOrder.verify(this.movieRepository, times(1)).findById(any());
        inOrder.verify(this.accountRepository, times(1)).findById(any());
        inOrder.verify(this.movieRepository, times(1)).save(any(Movie.class));
    }

    @Test
    public void retract_dislikes_shouldPass() {
        final String title = "title";
        final String description = "description";

        Movie movie = new Movie();
        movie.setId(1L);
        movie.setAccount(user1);
        movie.setDescription(description);
        movie.setTitle(title);
        movie.getDislikes().add(user2);

        //movie without likes and dislikes, other properties doesn't matter
        Movie expectedResult = new Movie();
        expectedResult.setId(1L);
        expectedResult.setAccount(user1);
        expectedResult.setDescription(description);
        expectedResult.setTitle(title);

        when(this.movieRepository.findById(any())).thenReturn(Optional.of(movie));
        when(this.accountRepository.findById(any())).thenReturn(Optional.of(user2));
        when(this.movieRepository.save(any(Movie.class))).thenReturn(movie);

        Movie actualResult = this.voteService.retract(1L, 1L);

        assertEquals(expectedResult, actualResult);

        InOrder inOrder = inOrder(this.accountRepository, this.movieRepository);
        inOrder.verify(this.movieRepository, times(1)).findById(any());
        inOrder.verify(this.accountRepository, times(1)).findById(any());
        inOrder.verify(this.movieRepository, times(1)).save(any(Movie.class));
    }
}
