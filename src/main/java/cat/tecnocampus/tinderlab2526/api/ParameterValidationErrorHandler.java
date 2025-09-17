package cat.tecnocampus.tinderlab2526.api;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.*;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;

@ControllerAdvice
public class ParameterValidationErrorHandler extends ResponseEntityExceptionHandler{

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ProblemDetail problemDetail = handleValidationException(ex);
        return ResponseEntity.status(status.value()).body(problemDetail);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex) {
        String details = getErrorsDetails(ex);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, details);
        problemDetail.setType(URI.create("http://localhost:8080/errors/bad-request"));
        problemDetail.setTitle("Bad request");
        problemDetail.setProperty("timestamp", Instant.now()); // adding more data using the Map properties of the ProblemDetail
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    private ProblemDetail handleValidationException(MethodArgumentNotValidException ex) {
        String details = getErrorsDetails(ex);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(ex.getStatusCode(), details);
        problemDetail.setType(URI.create("http://localhost:8080/errors/bad-request"));
        problemDetail.setTitle("Bad request");
        problemDetail.setInstance(ex.getBody().getInstance());
        problemDetail.setProperty("timestamp", Instant.now()); // adding more data using the Map properties of the ProblemDetail
        return problemDetail;
    }

    private String getErrorsDetails(MethodArgumentNotValidException ex) {
        return Optional.of(ex.getDetailMessageArguments())
                .map(args -> Arrays.stream(args)
                        .filter(msg -> !ObjectUtils.isEmpty(msg))
                        .reduce("Please make sure to provide a valid request, ", (a, b) -> a + " " + b)
                )
                .orElse("").toString();
    }

    private String getErrorsDetails(ConstraintViolationException ex) {
        return ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .reduce("Please make sure to provide a valid request, ", (a, b) -> a + " " + b);
    }
}

