package gr.assignment.movierama.domain.model.account;

import lombok.*;

import javax.persistence.Embeddable;
import java.io.Serializable;

import static lombok.AccessLevel.PUBLIC;

@Embeddable
@NoArgsConstructor(access = PUBLIC)
@AllArgsConstructor(staticName = "of")
@EqualsAndHashCode
@Getter
@Setter
public class AccountProfile implements Serializable {

    private String firstName;
    private String lastName;


    public String getFullName () {
        return String.join(" ", this.firstName, this.lastName);
    }
}
