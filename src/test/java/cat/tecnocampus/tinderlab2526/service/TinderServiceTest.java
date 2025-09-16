package cat.tecnocampus.tinderlab2526.service;

import cat.tecnocampus.tinderlab2526.application.TinderService;
import cat.tecnocampus.tinderlab2526.application.inputDTO.ProfileCommand;
import cat.tecnocampus.tinderlab2526.application.outputDTO.ProfileInformation;
import cat.tecnocampus.tinderlab2526.domain.Profile;
import cat.tecnocampus.tinderlab2526.persistence.ProfileRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TinderServiceTest {
    @Autowired
    private TinderService tinderService;
    @Autowired
    private ProfileRepository profileRepository;

    @Test
    public void getProfileByExistingIdTest() {
        ProfileInformation profile = tinderService.getProfileById(1L).orElseThrow();
        assertEquals(profile.getNickname(), "Alice");
    }

    @Test
    public void getProfileByNonExistingIdTest() {
        assertTrue(tinderService.getProfileById(100L).isEmpty());
    }

    @Test
    public void saveProfileTest() {
        ProfileCommand profileCommand = new ProfileCommand("paco@tecnocampus.cat", "Paco", "Man",
                "Bisexual", "Music");

        long before = profileRepository.count();
        Long id = tinderService.createProfile(profileCommand);
        long after = profileRepository.count();
        assertEquals(before + 1, after);
        assertNotNull(id);
        assertEquals("Paco", profileRepository.findById(after).orElseThrow().getNickname());
    }

    @Test
    @Transactional
    public void addLikeHappyNotMatchedTest() {
        tinderService.addLike(2L,3L);
        Profile origin = profileRepository.findByIdWithLikes(2L).orElseThrow();
        Profile target = profileRepository.findByIdWithLikes(3L).orElseThrow();

        assertTrue(origin.doesLike(target));
        assertFalse(target.doesLike(origin));
        assertFalse(origin.isMatched(target));
    }

    @Test
    @Transactional
    public void addLikeHappyMatchedTest() {
        tinderService.addLike(4L,3L);

        Profile origin = profileRepository.findByIdWithLikes(4L).orElseThrow();
        Profile target = profileRepository.findByIdWithLikes(3L).orElseThrow();

        assertTrue(origin.doesLike(target));
        assertTrue(target.doesLike(origin));
        assertTrue(origin.isMatched(target));
    }
}
