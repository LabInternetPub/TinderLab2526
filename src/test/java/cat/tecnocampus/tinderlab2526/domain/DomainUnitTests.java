package cat.tecnocampus.tinderlab2526.domain;

import cat.tecnocampus.tinderlab2526.domain.exceptions.IsNotCompatibleException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DomainUnitTests {

    @Test
    void CompatibleAttractionAndPassionTheSame() {
        Profile man = ProfilesMotherTest.ManAttractedByWomanPassionMusicProfiles(1L);
        Profile woman = ProfilesMotherTest.WomanAttractedByManPassionMusicProfiles(2L);
        assertTrue(man.isCompatible(woman));
    }

    @Test
    void IncompatibleAttractionSamePassionDifferent() {
        Profile man = ProfilesMotherTest.ManAttractedByWomanPassionMusicProfiles(1L);
        Profile woman = ProfilesMotherTest.WomanAttractedByManPassionDanceProfiles(2L);
        assertFalse(man.isCompatible(woman));
    }

    @Test
    void IncompatibleAttractionDifferentPassionSame() {
        Profile man = ProfilesMotherTest.ManAttractedByWomanPassionMusicProfiles(1L);
        Profile man2 = ProfilesMotherTest.ManAttractedByWomanPassionDanceProfiles(2L);
        assertFalse((man.isCompatible(man2)));
    }

    @Test
    void CompatibleAttractionBisexualPassionSame() {
        Profile man = ProfilesMotherTest.ManAttractedByWomanPassionMusicProfiles(1L);
        Profile bisexualWoman = ProfilesMotherTest.WomanAttractedByBisexualPassionMusicProfiles(2L);
        assertTrue(bisexualWoman.isCompatible(man));
    }

    @Test
    void IncompatibleAttractionBisexualPassionDifferent() {
        Profile man = ProfilesMotherTest.ManAttractedByWomanPassionDanceProfiles(1L);
        Profile bisexualWoman = ProfilesMotherTest.WomanAttractedByBisexualPassionMusicProfiles(2L);
        assertFalse(bisexualWoman.isCompatible(man));
    }

    @Test
    void IncompatibleToYourself() {
        Profile profile = ProfilesMotherTest.ManAttractedByWomanPassionMusicProfiles(1L);
        assertFalse(profile.isCompatible(profile));
    }

    @Test
    void likeToIncompatible() {
        Profile man = ProfilesMotherTest.ManAttractedByWomanPassionDanceProfiles(1L);
        Profile bisexualWoman = ProfilesMotherTest.WomanAttractedByBisexualPassionMusicProfiles(2L);
        // assert exception isNotCompatibleException is thrown
        Exception exception = assertThrows(IsNotCompatibleException.class, () -> {
            man.createAndMatchLike(bisexualWoman);
        });

        String expectedMessage = man.getNickname() + " is not compatible with " + bisexualWoman.getNickname();
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.equals(expectedMessage));
    }

    @Test
    void likeToCompatibleNotMatched() {
        Profile man = ProfilesMotherTest.ManAttractedByWomanPassionMusicProfiles(1L);
        Profile bisexualWoman = ProfilesMotherTest.WomanAttractedByBisexualPassionMusicProfiles(2L);
        man.createAndMatchLike(bisexualWoman);
        assertTrue(man.doesLike(bisexualWoman));
        assertFalse(man.isMatched(bisexualWoman));
    }

    @Test
    void likeToCompatibleAndMatched() {
        Profile man = ProfilesMotherTest.ManAttractedByWomanPassionMusicProfiles(1L);
        Profile bisexualWoman = ProfilesMotherTest.WomanAttractedByBisexualPassionMusicProfiles(2L);
        man.createAndMatchLike(bisexualWoman);
        bisexualWoman.createAndMatchLike(man);
        assertTrue(man.doesLike(bisexualWoman));
        assertTrue(man.isMatched(bisexualWoman));
        assertTrue(bisexualWoman.doesLike(man));
        assertTrue(bisexualWoman.isMatched(man));
    }
}