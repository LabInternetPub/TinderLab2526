package cat.tecnocampus.tinderlab2526.persistence;

import cat.tecnocampus.tinderlab2526.application.outputDTO.ProfileInformation;
import cat.tecnocampus.tinderlab2526.domain.Profile;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ProfileRepository extends CrudRepository<Profile, Long> {

    @Query("""
        SELECT p.id AS id, p.email AS email, p.nickname AS nickname, p.gender as gender, p.attraction as attraction, p.passion AS passion
        FROM Profile p
        WHERE p.id = :id
        """)
    Optional<ProfileInformation> findProfileInformationById(Long id);

    @Query("""
        SELECT p
        FROM Profile p
        LEFT JOIN FETCH p.likes l
        WHERE p.id = :id
        """)
    Optional<Profile> findByIdWithLikes(Long id);

    // find all profiles has the same gender that the origin attraction and the same passion that the origin passion. Exclude the origin profile
    @Query("""
    SELECT p.id AS id, p.email AS email, p.nickname AS nickname, p.gender AS gender, p.attraction AS attraction, p.passion AS passion
    FROM Profile p
    WHERE p.gender = :#{#origin.attraction}
      AND p.passion = :#{#origin.passion}
      AND p.id <> :#{#origin.id}
    """)
    List<ProfileInformation> findCandidatesByGenderAttractionAndPassion(Profile origin);
}
