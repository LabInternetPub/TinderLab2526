package cat.tecnocampus.tinderlab2526.application.mappers;

import cat.tecnocampus.tinderlab2526.domain.Like;
import cat.tecnocampus.tinderlab2526.application.outputDTO.LikeInformation;

public class LikeMapper {
    public static LikeInformation toLikeInformation(Like like) {
        return new LikeInformation(
            like.getTarget().getId(),
            like.getOrigin().getNickname(),
            like.getCreationDate(),
            like.isMatched(),
            like.getMatchDate()
        );
    }
}