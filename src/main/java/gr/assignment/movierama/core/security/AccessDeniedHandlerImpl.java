package gr.assignment.movierama.core.security;

import gr.assignment.movierama.domain.model.account.DelegatingUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {


    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug("Access to `" + request.getRequestURI() + "` denied.");
        }

        if (request.getRequestURI().startsWith("/api/")) {
            if (request.getUserPrincipal() instanceof DelegatingUser) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
            } else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } else {
            response.sendRedirect("/");
        }
    }
}