package gr.assignment.movierama.web.apis;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.assignment.movierama.core.model.exception.ApplicationException;
import gr.assignment.movierama.core.model.exception.EntityNotFoundException;
import gr.assignment.movierama.core.model.exception.common.BadRequestExceptionAssert;
import gr.assignment.movierama.core.model.exception.common.ResourceNotFoundExceptionAssert;
import gr.assignment.movierama.domain.application.service.VoteService;
import gr.assignment.movierama.domain.application.service.commands.AddVoteCommand;
import gr.assignment.movierama.domain.model.account.Account;
import gr.assignment.movierama.domain.model.account.AccountProfile;
import gr.assignment.movierama.domain.model.account.Username;
import gr.assignment.movierama.domain.model.movie.Movie;
import gr.assignment.movierama.web.request.AddVoteRequest;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class VoteApiIntegrationTests {
    private static final LocalDateTime NOW = LocalDateTime.now();

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VoteService voteService;

    private Movie movie;

    private Movie anotherMovie;

    private Account anotherAccount;

    private Account account;

    @Before
    public void setUp() {
        anotherAccount = new Account(Username.of("anotherUsername"), "password", AccountProfile.of("anotherFirstname", "anotherLastname"));
        anotherAccount.setId(1L);

        account = new Account(Username.of("username"), "password", AccountProfile.of("firstName", "lastName"));
        account.setId(0L);

        movie = new Movie();
        movie.setId(0L);
        movie.setAccount(account);
        movie.setCreatedDate(NOW);
        movie.setDescription("Description");
        movie.setTitle("A title");
        Set<Account> likes = new HashSet<>();
        likes.add(anotherAccount);
        movie.setLikes(likes);

        anotherMovie = new Movie();
        anotherMovie.setId(1L);
        anotherMovie.setAccount(anotherAccount);
        anotherMovie.setCreatedDate(NOW);
        anotherMovie.setDescription("Another Description");
        anotherMovie.setTitle("Another title");
        Set<Account> dislikes = new HashSet<>();
        dislikes.add(account);
        anotherMovie.setDislikes(dislikes);
    }


    @Test
    @WithMockUser(username = "0")
    public void vote_withValidData_shouldFail400UserAlreadyLike() throws Exception {
        long movieId = 0L;
        long authenticatedId = 0L;

        AddVoteRequest addVoteRequest = new AddVoteRequest();
        addVoteRequest.setIsLike(true);
        addVoteRequest.setMovieId(movieId);
        addVoteRequest.setPublisherId(authenticatedId);

        given(this.voteService.vote(authenticatedId, addVoteRequest.toCommand())).willThrow(new ApplicationException(BadRequestExceptionAssert.ALREADY_LIKE_MOVIE));

        this.mockMvc.perform(put("/api/vote").contentType(MediaType.APPLICATION_JSON).content(this.mapper.writeValueAsString(addVoteRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", Matchers.is("You already like this movie.")))
                .andExpect(jsonPath("status", Matchers.is(400)))
                .andExpect(jsonPath("code", Matchers.is(400203)))
                .andExpect(jsonPath("path", Matchers.is("/api/vote")));
    }

    @Test
    @WithMockUser(username = "1")
    public void vote_withValidData_shouldFail400UserAlreadyDislike() throws Exception {
        long movieId = 1L;
        long authenticatedId = 1L;

        AddVoteRequest addVoteRequest = new AddVoteRequest();
        addVoteRequest.setIsLike(false);
        addVoteRequest.setMovieId(movieId);
        addVoteRequest.setPublisherId(authenticatedId);

        given(this.voteService.vote(authenticatedId, addVoteRequest.toCommand())).willThrow(new ApplicationException(BadRequestExceptionAssert.ALREADY_DISLIKE_MOVIE));

        this.mockMvc.perform(put("/api/vote").contentType(MediaType.APPLICATION_JSON).content(this.mapper.writeValueAsString(addVoteRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", Matchers.is("You already dislike this movie.")))
                .andExpect(jsonPath("status", Matchers.is(400)))
                .andExpect(jsonPath("code", Matchers.is(400204)))
                .andExpect(jsonPath("path", Matchers.is("/api/vote")));
    }


    @Test
    @WithMockUser(username = "0")
    public void vote_withValidData_shouldFail400UserVoteTheMovieThatPosted() throws Exception {
        long movieId = 0L;
        long authenticatedId = 0L;

        AddVoteRequest addVoteRequest = new AddVoteRequest();
        addVoteRequest.setIsLike(true);
        addVoteRequest.setMovieId(movieId);
        addVoteRequest.setPublisherId(authenticatedId);

        given(this.voteService.vote(authenticatedId, addVoteRequest.toCommand())).willThrow(new ApplicationException(BadRequestExceptionAssert.VOTE_OWN_MOVIE));

        this.mockMvc.perform(put("/api/vote").contentType(MediaType.APPLICATION_JSON).content(this.mapper.writeValueAsString(addVoteRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", Matchers.is("You cannot vote your own movie.")))
                .andExpect(jsonPath("status", Matchers.is(400)))
                .andExpect(jsonPath("code", Matchers.is(400202)))
                .andExpect(jsonPath("path", Matchers.is("/api/vote")));
    }

    @Test
    @WithMockUser(username = "1")
    public void vote_withUnknownMovieId_shouldFail400() throws Exception {

        long unknownMovieId = 100L;

        AddVoteRequest addVoteRequest = new AddVoteRequest();
        addVoteRequest.setIsLike(false);
        addVoteRequest.setMovieId(unknownMovieId);
        addVoteRequest.setPublisherId(1L);

        given(this.voteService.vote(any(Long.class), any(AddVoteCommand.class))).willThrow(new EntityNotFoundException(ResourceNotFoundExceptionAssert.MOVIE, Collections.singletonMap("movieId", unknownMovieId)));

        this.mockMvc.perform(put("/api/vote").contentType(MediaType.APPLICATION_JSON).content(this.mapper.writeValueAsString(addVoteRequest)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message", Matchers.is("No movie was found.")))
                .andExpect(jsonPath("status", Matchers.is(404)))
                .andExpect(jsonPath("code", Matchers.is(404101)))
                .andExpect(jsonPath("path", Matchers.is("/api/vote")))
                .andExpect(jsonPath("$.data.movieId", Matchers.is(100)));
    }

    @Test
    @WithMockUser(username = "1")
    public void vote_withUnknownAuthenticateAccountId_shouldFail400() throws Exception {

        long unknownAuthenticatedAccountId = 100L;

        AddVoteRequest addVoteRequest = new AddVoteRequest();
        addVoteRequest.setIsLike(false);
        addVoteRequest.setMovieId(1L);
        addVoteRequest.setPublisherId(1L);

        given(this.voteService.vote(any(Long.class), any(AddVoteCommand.class))).willThrow(new EntityNotFoundException(ResourceNotFoundExceptionAssert.ACCOUNT, Collections.singletonMap("accountId", unknownAuthenticatedAccountId)));

        this.mockMvc.perform(put("/api/vote").contentType(MediaType.APPLICATION_JSON).content(this.mapper.writeValueAsString(addVoteRequest)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message", Matchers.is("No account was found.")))
                .andExpect(jsonPath("status", Matchers.is(404)))
                .andExpect(jsonPath("code", Matchers.is(404102)))
                .andExpect(jsonPath("path", Matchers.is("/api/vote")))
                .andExpect(jsonPath("$.data.accountId", Matchers.is(100)));
    }

    @Test
    @WithMockUser(username = "1")
    public void vote_withValidData_shouldSucceedWith200AndReturnTheUpdatedMovie() throws Exception {
        long movieId = 0L;
        long authenticatedId = 1L;

        this.movie.getLikes().remove(anotherAccount);
        this.movie.getDislikes().add(anotherAccount);

        AddVoteRequest addVoteRequest = new AddVoteRequest();
        addVoteRequest.setIsLike(false);
        addVoteRequest.setMovieId(movieId);
        addVoteRequest.setPublisherId(authenticatedId);

        given(this.voteService.vote(authenticatedId, addVoteRequest.toCommand())).willReturn(this.movie);

        this.mockMvc.perform(put("/api/vote").contentType(MediaType.APPLICATION_JSON).content(this.mapper.writeValueAsString(addVoteRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.likes.*", Matchers.hasSize(0)))
                .andExpect(jsonPath("$.dislikes.*", Matchers.hasSize(1)));
    }

    @Test
    @WithMockUser(username = "1")
    public void retract_withInValidData_shouldFailWith400NoVoteFoundForUser() throws Exception {
        long movieId = 0L;
        long authenticatedId = 1L;

        this.movie.getLikes().remove(anotherAccount);

        given(this.voteService.retract(authenticatedId, movieId)).willThrow(new EntityNotFoundException(ResourceNotFoundExceptionAssert.VOTE, Collections.singletonMap("accountId", authenticatedId)));

        this.mockMvc.perform(delete("/api/vote").contentType(MediaType.APPLICATION_JSON).queryParam("movieId", "" + movieId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message", Matchers.is("No vote was found.")))
                .andExpect(jsonPath("status", Matchers.is(404)))
                .andExpect(jsonPath("code", Matchers.is(404100)))
                .andExpect(jsonPath("path", Matchers.is("/api/vote")))
                .andExpect(jsonPath("$.data.accountId", Matchers.is(1)));
    }

    @Test
    @WithMockUser(username = "1")
    public void retract_withValidData_shouldReturn200WithUpdatedMovie() throws Exception {
        long movieId = 0L;
        long authenticatedId = 1L;

        this.movie.getLikes().remove(anotherAccount);

        given(this.voteService.retract(authenticatedId, movieId)).willReturn(this.movie);

        this.mockMvc.perform(delete("/api/vote").contentType(MediaType.APPLICATION_JSON).queryParam("movieId", "" + movieId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.likes.*", Matchers.hasSize(0)))
                .andExpect(jsonPath("$.dislikes.*", Matchers.hasSize(0)));
    }
}
