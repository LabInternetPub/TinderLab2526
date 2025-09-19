package cat.tecnocampus.tinderlab2526.api;

import cat.tecnocampus.tinderlab2526.application.exceptions.ProfileDoesNotExistException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.Instant;

@ControllerAdvice
public class ApplicationLevelExceptionsHandler {

    @ExceptionHandler(ProfileDoesNotExistException.class)
    @ResponseBody
    public ProblemDetail handleProfileDoesNotExistException(ProfileDoesNotExistException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setTitle("Profile Not Found");
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }
}
