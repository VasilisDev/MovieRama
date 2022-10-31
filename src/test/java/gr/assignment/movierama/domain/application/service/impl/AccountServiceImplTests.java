package gr.assignment.movierama.domain.application.service.impl;

import gr.assignment.movierama.core.model.exception.EntityAlreadyExistsException;
import gr.assignment.movierama.core.model.exception.EntityNotFoundException;
import gr.assignment.movierama.core.security.PasswordEncryptor;
import gr.assignment.movierama.domain.application.service.commands.RegistrationCommand;
import gr.assignment.movierama.domain.model.account.Account;
import gr.assignment.movierama.domain.model.account.AccountProfile;
import gr.assignment.movierama.infrastructure.repositories.AccountRepository;
import gr.assignment.movierama.domain.model.account.Username;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.dao.DuplicateKeyException;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AccountServiceImplTests {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private AccountRepository accountRepositoryMock;

    private PasswordEncryptor passwordEncryptor;

    private AccountServiceImpl accountServiceImpl;

    @Before
    public void setUp () {
        this.accountRepositoryMock = mock(AccountRepository.class);
        this.passwordEncryptor = mock(PasswordEncryptor.class);
        this.accountServiceImpl = new AccountServiceImpl(passwordEncryptor, accountRepositoryMock);
    }

    @Test
    public void findByUsername_nullUsername_shouldFail() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("`username` is required in order to proceed the action.");

        this.accountServiceImpl.findByUserName(null);

        verify(this.accountRepositoryMock, never()).findByUsernameValue("");
    }

    @Test
    public void findByUsername_emptyUsername_shouldFail() {
        thrown.expect(EntityNotFoundException.class);
        thrown.expectMessage("No account was found.");

        this.accountServiceImpl.findByUserName(Username.of(""));

        verify(this.accountRepositoryMock, never()).findByUsernameValue("");
    }

    @Test
    public void findByUsername_unknownUsername_shouldFail() {
        String unknownUsernameValue = "unknownUsername";
        Username unknownUsername = Username.of(unknownUsernameValue);

        when(this.accountRepositoryMock.findByUsername(unknownUsername)).thenReturn(Optional.empty());

        thrown.expect(EntityNotFoundException.class);
        thrown.expectMessage("No account was found.");

        this.accountServiceImpl.findByUserName(unknownUsername);

        verify(this.accountRepositoryMock, times(1)).findByUsername(unknownUsername);
    }

    @Test
    public void findByUsername_usernameAlreadyExists_shouldPass() {
        Username existingUsername = Username.of("ExistingUsername");
        Optional<Account> existingAccount = Optional.of(Account.of(existingUsername, "EncryptedPassword", null));

        when(this.accountRepositoryMock.findByUsername(existingUsername)).thenReturn(existingAccount);
        Account account = this.accountServiceImpl.findByUserName(existingUsername);
        verify(this.accountRepositoryMock, times(1)).findByUsername(existingUsername);

        assertNotNull(account);
        assertEquals(account, existingAccount.get());
    }

    @Test
    public void register_nullCommand_shouldFail()  {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("`RegistrationCommand` is required in order to proceed the action.");

        this.accountServiceImpl.register(null);
    }

    @Test
    public void register_existingUsername_shouldFail() {
        Username username = Username.of("existingUsername");
        String password = "password!";
        AccountProfile accountProfile = AccountProfile.of("Vasilis", "Tsakiris");

        doThrow(EntityAlreadyExistsException.class).when(this.accountRepositoryMock)
                .findByUsername(any());

        thrown.expect(EntityAlreadyExistsException.class);

        RegistrationCommand command = new RegistrationCommand(username, password, accountProfile);
        this.accountServiceImpl.register(command);

        verify(this.passwordEncryptor, never()).encode(any());
        verify(this.accountRepositoryMock, never()).save(any());
    }

    @Test
    public void register_duplicateKeyExceptionCatch_shouldFail() {
        Username username = Username.of("username");
        String password = "password!";
        AccountProfile accountProfile = AccountProfile.of("Vasilis", "Tsakiris");
        Account accountToBeSaved = Account.of(username, password, accountProfile);

        when(this.accountRepositoryMock.findByUsername(any())).thenReturn(Optional.empty());

        doThrow(DuplicateKeyException.class).when(this.accountRepositoryMock)
                .save(any());

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Error during creation of account.Might be a bug.");

        RegistrationCommand command = new RegistrationCommand(username, password, accountProfile);
        this.accountServiceImpl.register(command);

        verify(this.accountRepositoryMock, times(2)).findByUsername(any());
        verify(this.passwordEncryptor, times(1)).encode(any());
        verify(this.accountRepositoryMock, times(1)).save(any());
    }


    @Test
    public void register_validCommand_shouldSucceed()  {
        Username username = Username.of("username");
        String password = "password!";
        AccountProfile accountProfile = AccountProfile.of("Vasilis", "Tsakiris");
        Account accountToBeSaved = Account.of(username, password, accountProfile);

        when(this.accountRepositoryMock.findByUsername(any())).thenReturn(Optional.empty());
        when(this.accountRepositoryMock.save(any())).thenReturn(accountToBeSaved);

        RegistrationCommand command = new RegistrationCommand();
        command.setUsername(username);
        command.setAccountProfile(accountProfile);
        command.setPassword(password);

        this.accountServiceImpl.register(command);

        verify(this.accountRepositoryMock, times(1)).findByUsername(any());
        verify(this.passwordEncryptor, times(1)).encode(any());
        verify(this.accountRepositoryMock, times(1)).save(any());
    }
}
