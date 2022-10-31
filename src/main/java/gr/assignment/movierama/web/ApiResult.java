package gr.assignment.movierama.web;

import org.springframework.util.Assert;

import java.util.HashMap;

public class ApiResult extends HashMap<String, Object> {

    private static final long serialVersionUID = 877825499039674411L;

    public static ApiResult message(String message) {
        Assert.hasText(message, "Parameter `message` must not be blank");

        ApiResult apiResult = new ApiResult();
        apiResult.put("message", message);
        return apiResult;
    }
}
