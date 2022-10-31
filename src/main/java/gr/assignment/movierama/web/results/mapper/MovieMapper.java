package gr.assignment.movierama.web.results.mapper;

import gr.assignment.movierama.domain.model.account.Account;
import gr.assignment.movierama.domain.model.account.AccountProfile;
import gr.assignment.movierama.domain.model.movie.Movie;
import gr.assignment.movierama.web.results.MovieResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MovieMapper {

    private final AccountMapper accountMapper;

    public MovieResult toMovieResult(Movie m) {
        MovieResult movieResult = new MovieResult();
        movieResult.setMovieId(m.getId());
        final Account account = m.getAccount();
        movieResult.setPublisherId(account.getId());
        movieResult.setPublisherUsername(account.getUsername().getValue());
        final AccountProfile accountProfile = account.getAccountProfile();
        movieResult.setPublisherFullname(accountProfile.getFullName());
        movieResult.setCreatedDate(m.getCreatedDate());
        movieResult.setDescription(m.getDescription());
        movieResult.setTitle(m.getTitle());
        movieResult.setLikes(m.getLikes().stream().map(accountMapper::toAccountResult).collect(Collectors.toList()));
        movieResult.setDislikes(m.getDislikes().stream().map(accountMapper::toAccountResult).collect(Collectors.toList()));
        return movieResult;
    }
}
