package gr.assignment.movierama.web.apis;

import gr.assignment.movierama.domain.application.service.AccountService;
import gr.assignment.movierama.domain.model.account.Account;
import gr.assignment.movierama.domain.model.account.AccountProfile;
import gr.assignment.movierama.domain.model.account.Username;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AccountApiIntegrationTests {

    @Autowired
    private MockMvc mvcMock;

    @MockBean
    private AccountService accountService;

    @Test
    @WithMockUser(username = "1")
    public void getMyData_emptyAccountId_shouldFailAndReturn404() throws Exception {
        given(this.accountService.findById(any(Long.class))).willReturn(null);
        mvcMock.perform(get("/api/me"))
                .andExpect(status().is(404));
    }

    @Test
    @WithMockUser(username = "1")
    public void getMyData_validRequest_shouldPassAndReturn200WithTheDetailsOfAccount () throws Exception {
        long userId = 1L;
        Account account = new Account(Username.of("uname"), "password", AccountProfile.of("fname", "lname"));
        account.setId(userId);
        given(this.accountService.findById(userId)).willReturn(account);

        ResultActions actions = mvcMock.perform(get("/api/me"));

         actions.andExpect(status().isOk())
                 .andExpect(jsonPath("$.id").value(userId))
                 .andExpect(jsonPath("$.username").value(account.getUsername().getValue()))
                 .andExpect(jsonPath("$.firstName").value(account.getAccountProfile().getFirstName()))
                 .andExpect(jsonPath("$.lastName").value(account.getAccountProfile().getLastName()))
                 .andDo(print());
    }
}