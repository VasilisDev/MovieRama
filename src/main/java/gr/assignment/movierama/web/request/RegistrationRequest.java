package gr.assignment.movierama.web.request;

import gr.assignment.movierama.domain.model.account.AccountProfile;
import gr.assignment.movierama.domain.model.account.Username;
import gr.assignment.movierama.domain.application.service.commands.RegistrationCommand;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegistrationRequest {

    @NotNull(message = "Username must not be null")
    @Size(min = 2, max = 50, message = "Username must be between 2 and 50 characters")
    private String username;
    @NotNull(message = "Password must not be null")
    @Size(min = 6, max = 30, message = "Password must be between 6 and 30 characters")
    private String password;
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;

    public RegistrationCommand toCommand() {
        return new RegistrationCommand(Username.of(username), password, AccountProfile.of(firstName, lastName));
    }
}
