package gr.assignment.movierama.core.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import gr.assignment.movierama.core.model.exception.ApplicationException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestErrorResponse {

    private Integer code;
    private Integer status;
    private String message;
    private String path;
    private long timestamp;
    private HashMap<String, Object> data;

    public RestErrorResponse(ApplicationException ex, String path) {
        this(ex.getError().getCode(), ex.getError().getStatus().value(), ex.getError().getMessage(), path, ex.getData());
    }

    public RestErrorResponse(Exception ex, HttpStatus status, String path) {
        this(null, status.value(), ex.getMessage(), path, null);
    }

    public RestErrorResponse(HttpStatus httpStatus, String path) {
        this(null,httpStatus.value(), httpStatus.getReasonPhrase(),path,null);
    }

    public <T> RestErrorResponse(@Nullable Integer code, Integer status, @Nullable String message, String path, @Nullable Map<String, T> data) {
        this.code = code;
        this.status = status;
        this.message = message;
        this.path = path;
        this.timestamp = Instant.now().toEpochMilli();
        if (!ObjectUtils.isEmpty(data)) {
            this.data = new HashMap<>();
            this.data.putAll(data);
        }
    }
}