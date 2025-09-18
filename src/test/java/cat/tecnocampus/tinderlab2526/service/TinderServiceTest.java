package cat.tecnocampus.tinderlab2526.service;

import cat.tecnocampus.tinderlab2526.application.TinderService;
import cat.tecnocampus.tinderlab2526.application.inputDTO.ProfileCommand;
import cat.tecnocampus.tinderlab2526.application.outputDTO.ProfileInformation;
import cat.tecnocampus.tinderlab2526.domain.Profile;
import cat.tecnocampus.tinderlab2526.domain.exceptions.IsNotCompatibleException;
import cat.tecnocampus.tinderlab2526.persistence.ProfileRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Sql(statements = {"DELETE FROM like_profile", "DELETE FROM profile", "ALTER TABLE profile ALTER COLUMN id RESTART WITH 1"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:data-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
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
        assertEquals("Paco", profileRepository.findById(id).orElseThrow().getNickname());
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
        assertTrue(target.isMatched(origin));
    }

    @Test
    public void addLikeNotCompatibleTest() {
        Exception exception = assertThrows(IsNotCompatibleException.class, () -> {
            tinderService.addLike(1L,2L);
        });

        String expectedMessage = "is not compatible with";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void getCandidatesEmptyTest() {
        assertTrue(tinderService.getCandidates(6L).isEmpty());
    }

    @Test
    public void getCandidatesNotEmptyTest() {
        List<ProfileInformation> candidates = tinderService.getCandidates(3L);
        List<Long> ids = candidates.stream().map(ProfileInformation::getId).toList();

        assertEquals(2, candidates.size());
        assertThat(ids, containsInAnyOrder(2L, 4L));
    }

    @Test
    public void getCandidatesOriginNotIncludedTest() {
        List<ProfileInformation> candidates = tinderService.getCandidates(1L);

        assertEquals(1, candidates.size());
        assertEquals(5L, candidates.get(0).getId());
    }

    @Test
    public void getCandidatesNotExistingOriginTest() {
        Exception exception = assertThrows(cat.tecnocampus.tinderlab2526.application.exceptions.ProfileDoesNotExistException.class, () -> {
            tinderService.getCandidates(100L);
        });

        assertEquals("Origin profile with Id: 100 not found", exception.getMessage());
    }

    @Test void getProfileLikesTest() {
        var likes = tinderService.getProfileLikes(3L);
        assertEquals(1, likes.size());
        assertTrue(likes.stream().anyMatch(like -> like.targetId().equals(4L) && !like.matched()));
    }

    @Test
    public void getLikesNotExistingProfileTest() {
        Exception exception = assertThrows(cat.tecnocampus.tinderlab2526.application.exceptions.ProfileDoesNotExistException.class, () -> {
            tinderService.getProfileLikes(100L);
        });

        assertEquals("Profile with Id: 100 not found", exception.getMessage());
    }
}
