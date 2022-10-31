package gr.assignment.movierama.domain.model.movie;

import gr.assignment.movierama.domain.model.account.Account;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;;

@NoArgsConstructor
@Entity
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Table(name = "movie")
public class Movie implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @JoinColumn(name = "account_id")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private Account account;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "account_movie_likes",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "account_id"))
    private Set<Account> likes = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "account_movie_dislikes",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "account_id"))
    private Set<Account> dislikes = new HashSet<>();

    public static Movie create(Account account, String title, String description) {
        Movie movie = new Movie();
        movie.account = account;
        movie.title = title;
        movie.description = description;
        movie.createdDate = LocalDateTime.now();
        return movie;
    }
}