package cat.tecnocampus.tinderlab2526.application.inputDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

public record ProfileCommand(
        @Email
        String email,

        @Pattern(regexp = "^[A-Z][a-zA-Z]{2,}$", message = "Nickname must start with a capital letter and have at least 3 letters")
        String nickname,

        @Pattern(regexp = "^(Man|Woman|Bisexual|Indefinite)$", message = "Gender must be one of: Man, Woman, Bisexual, Idefinite")
        String gender,

        @Pattern(regexp = "^(Man|Woman|Bisexual|Indefinite)$", message = "Gender must be one of: Man, Woman, Bisexual, Indefinite")
        String attraction,

        @Pattern(regexp = "^(Sport|Music|Walk|Dance)$", message = "Passion must be one of: Sport, Music, Walk, Dance")
        String passion) {
}
