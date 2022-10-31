package gr.assignment.movierama.web.results;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MovieResult {
    private long movieId;
    private long publisherId;
    private String publisherUsername;
    private String publisherFullname;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;
    private String title;
    private String description;
    private List<AccountResult> likes;
    private List<AccountResult> dislikes;
}
