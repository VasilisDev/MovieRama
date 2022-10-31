package gr.assignment.movierama.web.results.mapper;

import gr.assignment.movierama.domain.model.account.Account;
import gr.assignment.movierama.domain.model.account.AccountProfile;
import gr.assignment.movierama.web.results.AccountResult;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public AccountResult toAccountResult (Account account) {
        AccountResult accountResult = new AccountResult();
        AccountProfile accountProfile = account.getAccountProfile();
        accountResult.setFirstName(accountProfile.getFirstName());
        accountResult.setLastName(accountProfile.getLastName());
        accountResult.setId(account.getId());
        accountResult.setUsername(account.getUsername().getValue());
        return accountResult;
    }
}
