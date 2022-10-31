package gr.assignment.movierama.web.request;

import gr.assignment.movierama.domain.application.service.commands.AddVoteCommand;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddVoteRequest {
    private long publisherId;
    private long movieId;
    private Boolean isLike;

    public AddVoteCommand toCommand() {
        return new AddVoteCommand(publisherId, movieId, isLike);
    }
}