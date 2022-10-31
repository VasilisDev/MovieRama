package gr.assignment.movierama.domain.application.service.commands;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public final class AddMovieCommand {

    private long accountId;
    private String title;
    private String description;
}
