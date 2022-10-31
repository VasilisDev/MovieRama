package gr.assignment.movierama.web.apis;

import gr.assignment.movierama.core.model.exception.ApplicationException;
import gr.assignment.movierama.core.security.CurrentUser;
import gr.assignment.movierama.domain.model.movie.Movie;
import gr.assignment.movierama.domain.application.service.VoteService;
import gr.assignment.movierama.web.request.AddVoteRequest;
import gr.assignment.movierama.web.results.mapper.MovieMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    private final MovieMapper movieMapper;

    @PutMapping("/vote")
    public HttpEntity<?> vote(@CurrentUser Long authenticatedUserId, @RequestBody AddVoteRequest request) throws ApplicationException {
        Movie movie = voteService.vote(authenticatedUserId, request.toCommand());
        return ResponseEntity.ok().body(movieMapper.toMovieResult(movie));
    }

    @DeleteMapping("/vote")
    public HttpEntity<?> retract(@CurrentUser Long authenticatedAccountId, @RequestParam Long movieId) throws ApplicationException {
        Movie movie = voteService.retract(authenticatedAccountId, movieId);
        return ResponseEntity.ok().body(movieMapper.toMovieResult(movie));
    }
}