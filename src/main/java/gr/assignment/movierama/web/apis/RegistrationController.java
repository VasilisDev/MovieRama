package gr.assignment.movierama.web.apis;

import gr.assignment.movierama.domain.application.service.AccountService;
import gr.assignment.movierama.core.model.exception.ApplicationException;
import gr.assignment.movierama.web.request.RegistrationRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class RegistrationController {

    private final AccountService accountService;

    public RegistrationController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/registrations")
    public ResponseEntity<?> register(@Valid @RequestBody RegistrationRequest registrationRequest) throws ApplicationException {
        accountService.register(registrationRequest.toCommand());
        return ResponseEntity.status(201).build();
    }
}