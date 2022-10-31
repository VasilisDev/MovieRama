package gr.assignment.movierama.domain.model.account;

import lombok.*;

import javax.persistence.Embeddable;
import java.io.Serializable;


@Embeddable
@Getter
@EqualsAndHashCode(of = "value")
@AllArgsConstructor(staticName = "of")
@ToString(of = "value")
@NoArgsConstructor
@Setter
public class Username implements Serializable {

   private String value;
}

