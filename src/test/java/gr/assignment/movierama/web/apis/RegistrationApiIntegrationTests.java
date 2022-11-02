package gr.assignment.movierama.web.apis;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.assignment.movierama.core.model.exception.EntityAlreadyExistsException;
import gr.assignment.movierama.core.model.exception.common.ResourceExistsExceptionAssert;
import gr.assignment.movierama.domain.application.service.AccountService;
import gr.assignment.movierama.web.request.RegistrationRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Collections;

import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class RegistrationApiIntegrationTests {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MockMvc mvcMock;

    @MockBean
    private AccountService accountService;

    private RegistrationRequest request(String username) {
        RegistrationRequest request = new RegistrationRequest();
        request.setUsername(username);
        request.setPassword("password");
        request.setFirstName("Firstname");
        request.setLastName("Lastname");
        return request;
    }

    @Test
    public void register_emptyBody_shouldFailAndReturn400() throws Exception {
        mvcMock.perform(post("/api/registrations"))
                .andExpect(status().is(400));
    }

    @Test
    public void register_existedUsername_shouldFailAndReturn400() throws Exception {
        RegistrationRequest request = request("exist");

        doThrow(new EntityAlreadyExistsException(ResourceExistsExceptionAssert.USERNAME, Collections.singletonMap("username", request.getUsername())))
                .when(accountService).register(request.toCommand());

        ResultActions actions = mvcMock.perform(
                post("/api/registrations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

         actions.andExpect(status().is(400))
                .andExpect(jsonPath("$.message").value("Username already exists!"))
                .andExpect(jsonPath("$.data.username").value(request.getUsername()));
    }

    @Test
    public void register_validRequest_shouldSucceedAndReturn201() throws Exception {
        RegistrationRequest request = request("bill");

         ResultActions actions = mvcMock.perform(
                post("/api/registrations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

        then(this.accountService).should().register(request.toCommand());

        actions.andExpect(status().is(201));

    }
}
