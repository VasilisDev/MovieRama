package gr.assignment.movierama.core.model.exception;

import gr.assignment.movierama.core.web.IErrorCodeProvider;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;


@Getter
@NoArgsConstructor
public class ApplicationException extends RuntimeException {
    private IErrorCodeProvider error;

    private final HashMap<String, Object> data = new HashMap<>();

    public ApplicationException(IErrorCodeProvider error) {
        this(error, null);
    }

    public ApplicationException(IErrorCodeProvider error, Map<String, Object> data) {
        this(error, null, data);
    }

    public ApplicationException(IErrorCodeProvider error, Throwable cause, Map<String, Object> data) {
        super(error.getMessage(), cause);
        this.error = error;
        if (!ObjectUtils.isEmpty(data)) {
            this.data.putAll(data);
        }
    }
}