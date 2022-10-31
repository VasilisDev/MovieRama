package gr.assignment.movierama.domain.application.service.impl;

import gr.assignment.movierama.core.model.exception.EntityAlreadyExistsException;
import gr.assignment.movierama.core.model.exception.EntityNotFoundException;
import gr.assignment.movierama.core.security.PasswordEncryptor;
import gr.assignment.movierama.domain.model.account.Account;
import gr.assignment.movierama.domain.model.account.AccountProfile;
import gr.assignment.movierama.infrastructure.repositories.AccountRepository;
import gr.assignment.movierama.domain.model.account.Username;
import gr.assignment.movierama.domain.application.service.AccountService;
import gr.assignment.movierama.domain.application.service.commands.RegistrationCommand;
import gr.assignment.movierama.core.model.exception.ApplicationException;
import gr.assignment.movierama.core.model.exception.common.ResourceNotFoundExceptionAssert;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;

import static gr.assignment.movierama.core.model.exception.common.ResourceExistsExceptionAssert.USERNAME;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountServiceImpl implements AccountService {
    private final PasswordEncryptor passwordEncryptor;

    private final AccountRepository accountRepository;

    @Override
    @Transactional(readOnly = true)
    public Account findById(long id) throws EntityNotFoundException {
        return accountRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundExceptionAssert.ACCOUNT.newException(Collections.singletonMap("accountId", id)));
    }

    @Override
    public void register(RegistrationCommand registrationCommand) throws EntityAlreadyExistsException {
        Assert.notNull(registrationCommand, "`RegistrationCommand` is required in order to proceed the action.");
         register(registrationCommand.getUsername(), registrationCommand.getPassword(), registrationCommand.getAccountProfile());
    }

    @Override
    @Transactional(readOnly = true)
    public Account findByUserName(Username username) throws EntityNotFoundException {
        Assert.notNull(username, "`username` is required in order to proceed the action.");
        return accountRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException(ResourceNotFoundExceptionAssert.ACCOUNT, Collections.singletonMap("username", username.getValue())));
    }

    private void register(Username username, String password, AccountProfile accountProfile) throws EntityAlreadyExistsException {
        USERNAME.isTrue(!isUsernameExists(username), Collections.singletonMap("username", username.getValue()));

        String encryptedPassword = passwordEncryptor.encode(password);
        Account account = Account.of(username, encryptedPassword, accountProfile);
        try {
            account = accountRepository.save(account);
        } catch (DuplicateKeyException dke) {
            account = accountRepository.findByUsername(username).orElse(null);
        }
        Assert.notNull(account, "Error during creation of account.Might be a bug.");
    }

    private boolean isUsernameExists(Username username) {
        Assert.notNull(username, "`Username` is required information.");
        Optional<Account> account = accountRepository.findByUsername(username);
        return account.isPresent();
    }
}