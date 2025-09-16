package cat.tecnocampus.tinderlab2526.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class LikePK implements Serializable {
    @Column(name = "origin_profile_id")
    Long originProfileId;

    @Column(name = "target_profile_id")
    Long targetProfileId;

    public LikePK() {
    }

    public LikePK(Long originProfileId, Long targetProfileId) {
        this.originProfileId = originProfileId;
        this.targetProfileId = targetProfileId;
    }

    public Long getOriginProfileId() {
        return originProfileId;
    }

    public Long getTargetProfileId() {
        return targetProfileId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LikePK)) return false;
        LikePK likePK = (LikePK) o;
        return originProfileId.equals(likePK.originProfileId) && targetProfileId.equals(likePK.targetProfileId);
    }

    @Override
    public int hashCode() {
        return 31 * originProfileId.hashCode() + targetProfileId.hashCode();
    }

}
