package gr.assignment.movierama.web.apis.authenticate;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.assignment.movierama.core.web.RestErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
@Slf4j
public class DefaultAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException {
        log.warn(e.getMessage());
        HttpStatus httpStatus = translateExceptionToHttpStatus(e);
        response.setStatus(httpStatus.value());
        response.setContentType(APPLICATION_JSON_VALUE);
        writeResponse(response.getWriter(), request, httpStatus, e);
    }

    protected HttpStatus translateExceptionToHttpStatus(AuthenticationException e) {
        return HttpStatus.UNAUTHORIZED;
    }

    protected void writeResponse(Writer writer, HttpServletRequest req, HttpStatus httpStatus, AuthenticationException e) throws IOException {
        RestErrorResponse restErrorResponse = new RestErrorResponse(e, httpStatus, req.getRequestURI());
        objectMapper.writeValue(writer, restErrorResponse);
    }
}