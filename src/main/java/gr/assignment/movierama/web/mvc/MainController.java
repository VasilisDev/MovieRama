package gr.assignment.movierama.web.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping(value = { "/","register","/login" })
    public String entry() {
        return "index";
    }

}
