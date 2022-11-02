package gr.assignment.movierama.web.apis;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.assignment.movierama.domain.application.service.MovieService;
import gr.assignment.movierama.domain.model.account.Account;
import gr.assignment.movierama.domain.model.account.AccountProfile;
import gr.assignment.movierama.domain.model.account.Username;
import gr.assignment.movierama.domain.model.movie.Movie;
import gr.assignment.movierama.web.request.AddMovieRequest;
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
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class MovieApiIntegrationTests {

    @Autowired
    private MockMvc mvcMock;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MovieService movieService;

    private Movie anotherMovie;

    private final Set<Movie> allMovies = new HashSet<>();

    private static final LocalDateTime NOW = LocalDateTime.now();

    private static final String TODAY_AS_STRING = NOW.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

    @Before
    public void setUp () {
        Account anotherAccount = new Account(Username.of("anotherUsername"), "password", AccountProfile.of("anotherFirstname", "anotherLastname"));
        anotherAccount.setId(1L);

        Account account = new Account(Username.of("username"), "password", AccountProfile.of("firstName", "lastName"));
        account.setId(0L);

        Movie movie = new Movie();
        movie.setId(0L);
        movie.setAccount(account);
        movie.setCreatedDate(NOW);
        movie.setDescription("Description");
        movie.setTitle("A title");
        Set<Account> likes = new HashSet<>();
        likes.add(anotherAccount);
        movie.setLikes(likes);

        this.anotherMovie = new Movie();
        anotherMovie.setId(1L);
        anotherMovie.setAccount(anotherAccount);
        anotherMovie.setCreatedDate(NOW);
        anotherMovie.setDescription("Another Description");
        anotherMovie.setTitle("Another title");
        Set<Account> dislikes = new HashSet<>();
        dislikes.add(account);
        anotherMovie.setDislikes(dislikes);

        this.allMovies.add(movie);
        this.allMovies.add(anotherMovie);
    }


    @Test
    public void getMoviesBy_EmptyUserName_ShouldReturnAllMovies() throws Exception {
        given(this.movieService.findAllBy(any(Optional.class)))
                .willReturn(new ArrayList<>(allMovies));

        ResultActions resultActions = this.mvcMock.perform(get("/api/movies"))
                .andExpect(status().isOk());

        resultActions
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.*", Matchers.isA(ArrayList.class)))
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$[*].movieId", Matchers.containsInAnyOrder(0, 1)))
                .andExpect(jsonPath("$[*].publisherId", Matchers.containsInAnyOrder(0, 1)))
                .andExpect(jsonPath("$[*].publisherUsername", Matchers.containsInAnyOrder("username", "anotherUsername")))
                .andExpect(jsonPath("$[*].publisherFullname", Matchers.containsInAnyOrder("firstName lastName", "anotherFirstname anotherLastname")))
                .andExpect(jsonPath("$[*].createdDate", Matchers.hasItem(TODAY_AS_STRING)))
                .andExpect(jsonPath("$[*].title", Matchers.containsInAnyOrder("A title", "Another title")))
                .andExpect(jsonPath("$[*].description", Matchers.containsInAnyOrder("Description", "Another Description")))
                .andExpect(jsonPath("$[*].likes.*", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[*].dislikes.*", Matchers.hasSize(1)))
                .andReturn();
    }

    @Test
    @WithMockUser(username = "1")
    public void getMoviesBy_validUserName_ShouldReturUserMovies() throws Exception {
        Optional<String> username = Optional.of("username");
        List<Movie> userMovies = allMovies.stream().filter(m -> m.getAccount().getUsername().getValue().equals(username.get())).collect(Collectors.toList());
        given(this.movieService.findAllBy(username))
                .willReturn(userMovies);

        ResultActions resultActions = this.mvcMock.perform(get("/api/movies").queryParam("username", username.get()))
                .andExpect(status().isOk());

        resultActions
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.*", Matchers.isA(ArrayList.class)))
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[0].movieId", Matchers.is(0)))
                .andExpect(jsonPath("$[0].publisherId", Matchers.is(0)))
                .andExpect(jsonPath("$[0].publisherUsername", Matchers.is("username")))
                .andExpect(jsonPath("$[0].publisherFullname", Matchers.is("firstName lastName")))
                .andExpect(jsonPath("$[0].createdDate", Matchers.startsWith(TODAY_AS_STRING)))
                .andExpect(jsonPath("$[0].title", Matchers.is("A title")))
                .andExpect(jsonPath("$[0].description", Matchers.is("Description")))
                .andExpect(jsonPath("$[0].likes.*", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].dislikes.*", Matchers.hasSize(0)))
                .andReturn();
    }

    @Test
    @WithMockUser(username = "1")
    public void create_withInvalidRequestBody_ShouldFailWith401 () throws Exception {
        AddMovieRequest addMovieRequest = new AddMovieRequest();
        addMovieRequest.setDescription(null);
        addMovieRequest.setTitle(null);

        this.mvcMock.perform(post("/api/movies")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(addMovieRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "1")
    public void create_withValidRequestBody_ShouldFailWith401 () throws Exception {
        long userId = 1L;

        AddMovieRequest addMovieRequest = new AddMovieRequest();
        addMovieRequest.setDescription("a title");
        addMovieRequest.setTitle("a description");

        given(this.movieService.create(addMovieRequest.toCommand(userId))).willReturn(this.anotherMovie);

        ResultActions actions = this.mvcMock.perform(post("/api/movies")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(addMovieRequest)));

        actions.andExpect(status().isCreated())
                .andExpect(jsonPath("$.movieId", Matchers.is(1)))
                .andExpect(jsonPath("$.publisherId", Matchers.is(1)))
                .andExpect(jsonPath("$.publisherUsername", Matchers.is("anotherUsername")))
                .andExpect(jsonPath("$.publisherFullname", Matchers.is("anotherFirstname anotherLastname")))
                .andExpect(jsonPath("$.createdDate", Matchers.startsWith(TODAY_AS_STRING)))
                .andExpect(jsonPath("$.title", Matchers.is("Another title")))
                .andExpect(jsonPath("$.description", Matchers.is("Another Description")))
                .andExpect(jsonPath("$.likes.*", Matchers.hasSize(0)))
                .andExpect(jsonPath("$.dislikes.*", Matchers.hasSize(1)))
                .andReturn();
    }
}
