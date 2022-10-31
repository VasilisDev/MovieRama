package gr.assignment.movierama.core.security;

import gr.assignment.movierama.domain.model.account.DelegatingUser;
import gr.assignment.movierama.domain.model.account.Username;
import gr.assignment.movierama.domain.application.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

@RequiredArgsConstructor
public class DelegatingUserService implements UserDetailsService {

    private final AccountService accountService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return Optional.ofNullable(accountService.findByUserName(Username.of(username)))
                .map(DelegatingUser::new)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }
}