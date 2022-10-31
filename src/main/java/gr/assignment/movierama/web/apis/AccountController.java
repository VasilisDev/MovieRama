package gr.assignment.movierama.web.apis;

import gr.assignment.movierama.core.model.exception.ApplicationException;
import gr.assignment.movierama.domain.application.service.AccountService;
import gr.assignment.movierama.core.security.CurrentUser;
import gr.assignment.movierama.web.results.mapper.AccountMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    private final AccountMapper accountMapper;

    @GetMapping("/me")
    public HttpEntity<?> getMyData(@CurrentUser Long accountId) throws ApplicationException {
        return Optional.ofNullable(accountService.findById(accountId))
                .map(accountMapper::toAccountResult)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
