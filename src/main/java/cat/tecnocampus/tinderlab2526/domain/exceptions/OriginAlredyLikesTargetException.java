package cat.tecnocampus.tinderlab2526.domain.exceptions;

import cat.tecnocampus.tinderlab2526.domain.Profile;

public class OriginAlredyLikesTargetException  extends RuntimeException{
    public OriginAlredyLikesTargetException(Profile origin, Profile target) {
        super(origin.getNickname() + " already likes " + target.getNickname());
    }
}
