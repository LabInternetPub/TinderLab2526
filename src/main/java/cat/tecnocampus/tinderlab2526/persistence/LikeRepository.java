package cat.tecnocampus.tinderlab2526.persistence;

import cat.tecnocampus.tinderlab2526.application.outputDTO.LikeInformation;
import cat.tecnocampus.tinderlab2526.domain.Like;
import cat.tecnocampus.tinderlab2526.domain.LikePK;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface LikeRepository extends CrudRepository<Like, LikePK> {

    @Query("""
         SELECT l.origin.id AS originId, l.origin.nickname AS originNickname, 
                l.target.id AS targetId, l.target.nickname AS targetNickname,
                l.creationDate AS creationDate, l.matched AS matched, l.matchDate AS matchDate
         FROM Like l
         WHERE l.id.originProfileId = :originId
""")
    public List<LikeInformation> findByOriginId(Long originId);
}
