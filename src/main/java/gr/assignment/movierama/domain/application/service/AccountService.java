package gr.assignment.movierama.domain.application.service;

import gr.assignment.movierama.core.model.exception.EntityAlreadyExistsException;
import gr.assignment.movierama.core.model.exception.EntityNotFoundException;
import gr.assignment.movierama.domain.application.service.commands.RegistrationCommand;
import gr.assignment.movierama.domain.model.account.Account;
import gr.assignment.movierama.domain.model.account.Username;

public interface AccountService {

    Account findById(long id) throws EntityNotFoundException;

    void register(RegistrationCommand registrationCommand) throws EntityAlreadyExistsException;

    Account findByUserName(Username username) throws EntityNotFoundException ;
}
