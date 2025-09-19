package cat.tecnocampus.tinderlab2526.api;

import cat.tecnocampus.tinderlab2526.domain.exceptions.IsNotCompatibleException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.Instant;

@ControllerAdvice
public class DomainLevelExceptionsHandler {

    @ExceptionHandler(IsNotCompatibleException.class)
    @ResponseBody
    public ProblemDetail handleIsNotCompatibleException(IsNotCompatibleException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        pd.setTitle("Profiles are not compatible");
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }
}
