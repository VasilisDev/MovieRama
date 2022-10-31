package gr.assignment.movierama.domain.model.movie;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class Vote implements Serializable {

    private long movieId;
    private boolean hasLikeMovie;

}
