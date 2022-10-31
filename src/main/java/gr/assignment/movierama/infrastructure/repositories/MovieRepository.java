package gr.assignment.movierama.infrastructure.repositories;

import gr.assignment.movierama.core.orm.BaseRepository;
import gr.assignment.movierama.domain.model.movie.Movie;

import java.util.List;
import java.util.Optional;

public interface MovieRepository extends BaseRepository<Movie, Long> {

    List<Movie> findMoviesByAccount_UsernameValue(String username);

    Optional<Movie> findByTitle(String title);
}
