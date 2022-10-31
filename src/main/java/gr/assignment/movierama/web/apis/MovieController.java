package gr.assignment.movierama.web.apis;

import gr.assignment.movierama.core.model.exception.ApplicationException;
import gr.assignment.movierama.core.security.CurrentUser;
import gr.assignment.movierama.domain.model.movie.Movie;
import gr.assignment.movierama.domain.application.service.MovieService;
import gr.assignment.movierama.web.request.AddMovieRequest;
import gr.assignment.movierama.web.results.MovieResult;
import gr.assignment.movierama.web.results.mapper.MovieMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    private final MovieMapper movieMapper;

    @PostMapping("/movies")
    public HttpEntity<?> create(@Valid @RequestBody AddMovieRequest addMovieRequest, @CurrentUser Long accountId) throws ApplicationException {
        Movie movie = movieService.create(addMovieRequest.toCommand(accountId));
        return ResponseEntity.status(201).body(movieMapper.toMovieResult(movie));
    }

    @GetMapping("/movies")
    public HttpEntity<?> getMoviesBy(@RequestParam(value = "username", required = false) Optional<String> username) throws ApplicationException {
        List<MovieResult> movies = movieService.findAllBy(username).stream().map(movieMapper::toMovieResult).collect(Collectors.toList());
        return ResponseEntity.ok(movies);
    }
}