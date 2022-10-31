package gr.assignment.movierama.domain.application.service.commands;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public final class AddVoteCommand {

    private long publisherId;
    private long movieId;
    private boolean isLike;
}
