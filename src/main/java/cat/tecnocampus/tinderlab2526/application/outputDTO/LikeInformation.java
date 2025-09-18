package cat.tecnocampus.tinderlab2526.application.outputDTO;

import java.time.LocalDate;

public record LikeInformation(
        Long targetId,
        String targetNickname,
        LocalDate createdDate,
        boolean matched,
        LocalDate matchDate
) {}
