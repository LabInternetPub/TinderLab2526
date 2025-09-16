package cat.tecnocampus.tinderlab2526.domain;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "like_profile")
public class Like {

    @EmbeddedId
    private LikePK id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="origin_profile_id", referencedColumnName="id")
    @MapsId("origin_profile_id")
    private Profile origin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="target_profile_id", referencedColumnName="id")
    @MapsId("target_profile_id")
    private Profile target;

    private boolean matched;
    private LocalDate creationDate;
    private LocalDate matchDate;

    public Like() {
    }

    public Like(Profile origin, Profile target) {
        id = new LikePK(origin.getId(), target.getId());
        this.origin = origin;
        this.target = target;
        creationDate = LocalDate.now();
        matched = false;
        matchDate = null;
    }

    public Profile getTarget() {
        return target;
    }

    public Profile getOrigin() {
        return origin;
    }

    public void setOrigin(Profile origin) {
        this.origin = origin;
    }

    public void setTarget(Profile target) {
        this.target = target;
    }

    public boolean isMatched() {
        return matched;
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
        this.matchDate = LocalDate.now();
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate date) {
        this.creationDate = date;
    }

    public LocalDate getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(LocalDate matchDate) {
        this.matchDate = matchDate;
    }


}
