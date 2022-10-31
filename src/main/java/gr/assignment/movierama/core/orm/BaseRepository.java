package gr.assignment.movierama.core.orm;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface BaseRepository<E , ID> extends Repository<E, ID> {

    Optional<E> findById(ID id);

    E save(E entity);

    List<E> findAll();

    default E update(E a) {
        return save(a);
    }

    void delete(E a);

    default void deleteById(ID id) {
        Optional<E> aOptional = findById(id);
        aOptional.ifPresent(this::delete);
    }

    void flush();
}
