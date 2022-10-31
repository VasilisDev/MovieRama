package gr.assignment.movierama.domain.model.account;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Optional;

@NoArgsConstructor
@Entity
@Setter
@Getter
@EqualsAndHashCode
@ToString
@Table(name = "account")
public class Account implements Serializable {
    private static final long serialVersionUID = -1266334577962194584L;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;

    @AttributeOverride(name = "value", column = @Column(name = "username", length = 100, unique = true))
    @Embedded
    private Username username;

    @Column(name = "password", length = 100)
    private String encryptedPassword;

    @AttributeOverrides({
            @AttributeOverride(name = "firstName", column = @Column(name = "first_name", length = 50)),
            @AttributeOverride(name = "lastName", column = @Column(name = "last_name", length = 50)),
    })
    @Embedded
    private AccountProfile accountProfile;

    public Account(Username username, String encryptedPassword, AccountProfile accountProfile) {
        this.username = username;
        this.encryptedPassword = encryptedPassword;
        this.accountProfile = accountProfile;
    }

    public static Account of(Username username, String encryptedPassword, AccountProfile accountProfile) {
        return new Account(username, encryptedPassword, accountProfile);
    }

    public Username getUsername() {
        return username;
    }

    public Optional<String> getEncryptedPassword() {
        return Optional.ofNullable(encryptedPassword);
    }
}