package gr.assignment.movierama.web.request;

import gr.assignment.movierama.domain.application.service.commands.AddMovieCommand;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class AddMovieRequest {

    @NotNull
    @NotBlank(message = "title must not be blank or empty")
    private String title;

    @NotBlank(message = "description must not be blank or empty")
    @NotNull
    private String description;


    public AddMovieCommand toCommand (long accountId) {
        return new AddMovieCommand(accountId, title, description);
    }
}
