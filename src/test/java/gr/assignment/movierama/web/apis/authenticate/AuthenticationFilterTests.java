package gr.assignment.movierama.web.apis.authenticate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class AuthenticationFilterTests {
    public static final String API_AUTHENTICATIONS = "/api/authentications";

    public static final String HTTP_METHOD = "POST";

    @MockBean
    private AuthenticationManager authenticationManagerMock;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test(expected = InternalAuthenticationServiceException.class)
    public void attemptAuthentication_emptyRequestBody_shouldFail() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest(HTTP_METHOD, API_AUTHENTICATIONS);
        AuthenticationFilter filter = new AuthenticationFilter(API_AUTHENTICATIONS, objectMapper);
        filter.setAuthenticationManager(authenticationManagerMock);
        filter.attemptAuthentication(request, new MockHttpServletResponse());
    }

    @Test(expected = InternalAuthenticationServiceException.class)
    public void attemptAuthentication_invalidJsonStringRequestBody_shouldFail() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest(HTTP_METHOD, API_AUTHENTICATIONS);
        request.setContent("username=testusername&password=TestPassword!".getBytes());
        AuthenticationFilter filter = new AuthenticationFilter(API_AUTHENTICATIONS, objectMapper);
        filter.setAuthenticationManager(authenticationManagerMock);
        filter.attemptAuthentication(request, new MockHttpServletResponse());
    }

    @Test
    public void attemptAuthentication_validJsonStringRequestBody_shouldSucceed() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest(HTTP_METHOD, API_AUTHENTICATIONS);
        request.setContent("{\"username\": \"testusername\", \"password\": \"TestPassword!\"}".getBytes());
        AuthenticationFilter filter = new AuthenticationFilter(API_AUTHENTICATIONS, objectMapper);
        filter.setAuthenticationManager(authenticationManagerMock);
        filter.attemptAuthentication(request, new MockHttpServletResponse());
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken("testusername", "TestPassword!");
        verify(authenticationManagerMock).authenticate(token);
    }
}