package gr.assignment.movierama.infrastructure.repositories;

import gr.assignment.movierama.core.orm.BaseRepository;
import gr.assignment.movierama.domain.model.account.Account;
import gr.assignment.movierama.domain.model.account.Username;

import java.util.Optional;

public interface AccountRepository extends BaseRepository<Account, Long> {

    Optional<Account> findByUsername(Username username);

    Optional<Account> findByUsernameValue(String username);
}
