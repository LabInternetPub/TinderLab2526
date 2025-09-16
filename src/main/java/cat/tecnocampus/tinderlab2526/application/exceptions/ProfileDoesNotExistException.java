package cat.tecnocampus.tinderlab2526.application.exceptions;

public class ProfileDoesNotExistException extends RuntimeException {
    public ProfileDoesNotExistException(String message) {
        super(message);
    }
}
