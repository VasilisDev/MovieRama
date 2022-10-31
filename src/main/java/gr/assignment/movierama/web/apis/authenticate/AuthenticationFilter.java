package gr.assignment.movierama.web.apis.authenticate;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

public class AuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final ObjectMapper objectMapper;

    public AuthenticationFilter(String authenticationProcessingUrl, ObjectMapper objectMapper) {
        super(new AntPathRequestMatcher(authenticationProcessingUrl, "POST"));
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (!"POST".equals(request.getMethod())) {
            throw new AuthenticationServiceException(String.format("Method %s not supported.", request.getMethod()));
        }
        try {
            LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
            String username = Optional.ofNullable(loginRequest.getUsername())
                    .map(String::trim)
                    .orElse("");
            String password = Optional.ofNullable(loginRequest.getPassword())
                    .orElse("");
            return this.getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalAuthenticationServiceException(e.getMessage(), e);
        }
    }

    @Data
    private static class LoginRequest {
        String username;
        String password;
    }
}
