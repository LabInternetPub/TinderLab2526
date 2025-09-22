package cat.tecnocampus.tinderlab2526.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class SecurityExampleController {
    @GetMapping("/helloWorld")
    public String helloWorld() {
        return "Hello World";
    }

    @GetMapping("/helloAdmin")
    public String helloAdmin() {
        return "Hello Admin";
    }

    @GetMapping("/helloUser")
    public String helloUser() {
        return "Hello User";
    }

    @GetMapping("/helloUserAdmin")
    public String helloUserAdmin() {
        return "Hello User or Admin";
    }

    @GetMapping("/hello/me")
    public String helloMe(Principal principal) {
        return "Hello " + principal.getName();
    }
}
