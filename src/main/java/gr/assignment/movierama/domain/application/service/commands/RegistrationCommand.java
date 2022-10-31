package gr.assignment.movierama.domain.application.service.commands;

import gr.assignment.movierama.domain.model.account.AccountProfile;
import gr.assignment.movierama.domain.model.account.Username;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegistrationCommand {
    Username username;
    String password;
    AccountProfile accountProfile;
}
