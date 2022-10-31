package gr.assignment.movierama.web.apis.authenticate;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.assignment.movierama.web.ApiResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
@Slf4j
public class DefaultAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        log.debug("User has successfully been authenticated");
        response.setContentType(APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), ApiResult.message("authenticated"));
    }
}