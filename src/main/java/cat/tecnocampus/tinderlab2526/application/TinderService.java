package cat.tecnocampus.tinderlab2526.application;

import cat.tecnocampus.tinderlab2526.application.exceptions.ProfileDoesNotExistException;
import cat.tecnocampus.tinderlab2526.application.inputDTO.ProfileCommand;
import cat.tecnocampus.tinderlab2526.application.outputDTO.ProfileInformation;
import cat.tecnocampus.tinderlab2526.domain.Profile;
import cat.tecnocampus.tinderlab2526.persistence.ProfileRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import cat.tecnocampus.tinderlab2526.application.mappers.ProfileMapper;

import java.util.Optional;


@Service
public class TinderService {
    private final ProfileRepository profileRepository;

    public TinderService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public Optional<ProfileInformation> getProfileById(Long id) {
        return profileRepository.findProfileInformationById(id);
    }

    public Long createProfile(ProfileCommand profileCommand) {
        Profile profile = ProfileMapper.inputProfileToDomain(profileCommand);
        Profile savedProfile = this.profileRepository.save(profile);
        return savedProfile.getId();
    }

    @Transactional
    public void addLike(Long originId, Long targetId) {
        Profile origin = profileRepository.findByIdWithLikes(originId).orElseThrow(() -> new ProfileDoesNotExistException("Origin profile with Id: " + originId + " not found"));
        Profile target = profileRepository.findByIdWithLikes(targetId).orElseThrow(() -> new ProfileDoesNotExistException("Target profile with Id: " + targetId + " not found"));

        origin.createAndMatchLike(target);
    }
}
