package cat.tecnocampus.tinderlab2526.domain.exceptions;

import cat.tecnocampus.tinderlab2526.domain.Profile;

public class IsNotCompatibleException extends RuntimeException {
    public IsNotCompatibleException(Profile origin, Profile target) {
        super(origin.getNickname() + " is not compatible with " + target.getNickname());
    }
}
