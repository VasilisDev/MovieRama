package gr.assignment.movierama.domain.application.service.impl;

import gr.assignment.movierama.core.model.exception.EntityAlreadyExistsException;
import gr.assignment.movierama.core.model.exception.EntityNotFoundException;
import gr.assignment.movierama.domain.application.service.commands.AddMovieCommand;
import gr.assignment.movierama.domain.model.account.Account;
import gr.assignment.movierama.domain.model.account.AccountProfile;
import gr.assignment.movierama.infrastructure.repositories.AccountRepository;
import gr.assignment.movierama.domain.model.account.Username;
import gr.assignment.movierama.domain.model.movie.Movie;
import gr.assignment.movierama.infrastructure.repositories.MovieRepository;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MovieServiceImplTests {

    private AccountRepository accountRepository;

    private MovieRepository movieRepository;

    private MovieServiceImpl movieService;


    @Before
    public void beforeSetup() {
        this.accountRepository = mock(AccountRepository.class);
        this.movieRepository = mock(MovieRepository.class);
        this.movieService = new MovieServiceImpl(movieRepository, accountRepository);
    }

    @Test
    public void findAllBy_emptyUsername_shouldReturnAllMovies() {
        Optional<String> emptyUsername = Optional.empty();

        Movie movie = new Movie();
        movie.setAccount(new Account(Username.of("username"), "password", AccountProfile.of("firstNAme", "lastname")));
        movie.setCreatedDate(LocalDateTime.now());
        movie.setDescription("Description");
        movie.setTitle("A title");

        List<Movie> movies = new ArrayList<>();
        movies.add(movie);

        when(movieRepository.findAll()).thenReturn(movies);

        List<Movie> result = this.movieService.findAllBy(emptyUsername);

        assertEquals(movies, result);

        verify(this.movieRepository, never()).findMoviesByAccount_UsernameValue(any());
        verify(this.movieRepository, timeout(1)).findAll();
    }

    @Test
    public void findAllBy_filledUsername_shouldReturnUserMovies() {
        Optional<String> username = Optional.of("username");

        Movie userMovie = new Movie();
        userMovie.setAccount(new Account(Username.of("username"), "password", AccountProfile.of("firstNAme", "lastname")));
        userMovie.setCreatedDate(LocalDateTime.now());
        userMovie.setDescription("Description");
        userMovie.setTitle("A title");

        Movie anotherMovie = new Movie();
        anotherMovie.setAccount(new Account(Username.of("a"), "password", AccountProfile.of("anotherFirstname", "anotherLastname")));
        anotherMovie.setCreatedDate(LocalDateTime.now());
        anotherMovie.setDescription("A Description");
        anotherMovie.setTitle("Another title");

        List<Movie> allMovies = new ArrayList<>();
        allMovies.add(userMovie);
        allMovies.add(anotherMovie);

        List<Movie> userMovies = new ArrayList<>();
        userMovies.add(userMovie);

        when(movieRepository.findMoviesByAccount_UsernameValue(username.get())).thenReturn(userMovies);

        List<Movie> result = this.movieService.findAllBy(username);

        assertNotEquals(allMovies, result);
        assertEquals(userMovies, result);

        verify(this.movieRepository, times(1)).findMoviesByAccount_UsernameValue(username.get());
        verify(this.movieRepository, never()).findAll();
    }

    @Test
    public void create_nullCommand_shouldThrowException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            this.movieService.create(null);
        });

        assertEquals(ex.getMessage(), "`addMovieCommand` is required in order to proceed the action.");
    }

    @Test
    public void create_movieTitleExists_shouldThrowException() {
        String existingTitle = "existingTitle";
        AddMovieCommand command = new AddMovieCommand(0L, existingTitle, null);

        when(this.movieRepository.findByTitle(existingTitle)).thenReturn(Optional.of(new Movie()));

        EntityAlreadyExistsException ex = assertThrows(EntityAlreadyExistsException.class, () -> {
            this.movieService.create(command);
        });

        assertEquals(ex.getMessage(), "Movie already exists!");

        verify(this.movieRepository, times(1)).findByTitle(existingTitle);
        verify(this.accountRepository, times(0)).findById(any());
        verify(this.movieRepository, times(0)).save(any());
    }

    @Test
    public void create_publisherNotFound_shouldThrowException() {
        long accountId = 1L;
        AddMovieCommand command = new AddMovieCommand(accountId, "a title", "a description");

        when(this.movieRepository.findByTitle(any())).thenReturn(Optional.empty());
        when(this.accountRepository.findById(accountId)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> {
            this.movieService.create(command);
        });

        assertEquals(ex.getMessage(), "No account was found.");

        InOrder inOrder = inOrder(this.movieRepository, this.accountRepository);
        inOrder.verify(this.movieRepository, times(1)).findByTitle(any());
        inOrder.verify(this.accountRepository, times(1)).findById(accountId);
        verify(this.movieRepository, times(0)).save(any());
    }

    @Test
    public void create_validCommand_shouldPass() throws IllegalAccessException {
        long accountId = 1L;
        AddMovieCommand command = new AddMovieCommand(accountId, "a title", "a description");

        Account publisher = new Account(Username.of("username"), "password", AccountProfile.of("firstname", "lastname"));
        FieldUtils.writeField(publisher, "id", accountId, true);

        Movie expectedMovie = new Movie();
        expectedMovie.setTitle(command.getTitle());
        expectedMovie.setDescription(command.getDescription());
        expectedMovie.setAccount(publisher);

        when(this.movieRepository.findByTitle(any(String.class))).thenReturn(Optional.empty());
        when(this.accountRepository.findById(accountId)).thenReturn(Optional.of(publisher));
        when(this.movieRepository.save(any(Movie.class))).thenReturn(expectedMovie);

        Movie actualMovie = this.movieService.create(command);

        assertEquals(expectedMovie, actualMovie);

        InOrder inOrder = inOrder(this.accountRepository, this.movieRepository);
        inOrder.verify(this.movieRepository, times(1)).findByTitle(any(String.class));
        inOrder.verify(this.accountRepository, times(1)).findById(accountId);
        inOrder.verify(this.movieRepository, times(1)).save(any(Movie.class));
    }
}