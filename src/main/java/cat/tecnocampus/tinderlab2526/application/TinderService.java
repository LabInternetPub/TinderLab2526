package cat.tecnocampus.tinderlab2526.application;

import cat.tecnocampus.tinderlab2526.application.exceptions.ProfileDoesNotExistException;
import cat.tecnocampus.tinderlab2526.application.inputDTO.ProfileCommand;
import cat.tecnocampus.tinderlab2526.application.mappers.LikeMapper;
import cat.tecnocampus.tinderlab2526.application.outputDTO.LikeInformation;
import cat.tecnocampus.tinderlab2526.application.outputDTO.ProfileInformation;
import cat.tecnocampus.tinderlab2526.domain.Profile;
import cat.tecnocampus.tinderlab2526.persistence.LikeRepository;
import cat.tecnocampus.tinderlab2526.persistence.ProfileRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import cat.tecnocampus.tinderlab2526.application.mappers.ProfileMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class TinderService {
    private final ProfileRepository profileRepository;
    private final LikeRepository likeRepository;

    public TinderService(ProfileRepository profileRepository, LikeRepository likeRepository) {
        this.profileRepository = profileRepository;
        this.likeRepository = likeRepository;
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
        Profile origin = profileRepository.findByIdWithLikes(originId)
                .orElseThrow(() -> new ProfileDoesNotExistException("Origin profile with Id: " + originId + " not found"));
        Profile target = profileRepository.findByIdWithLikes(targetId)
                .orElseThrow(() -> new ProfileDoesNotExistException("Target profile with Id: " + targetId + " not found"));

        origin.createAndMatchLike(target);
    }

    public List<ProfileInformation> getCandidates(Long originId) {
        Profile origin = profileRepository.findById(originId)
                .orElseThrow(() -> new ProfileDoesNotExistException("Origin profile with Id: " + originId + " not found"));
        return profileRepository.findCandidatesByGenderAttractionAndPassion(origin);
    }

    public List<LikeInformation> getProfileLikes(Long profileId) {
        Profile profile = profileRepository.findByIdWithLikes(profileId)
                .orElseThrow(() -> new ProfileDoesNotExistException("Profile with Id: " + profileId + " not found"));
        return profile.getLikes().stream().map(like -> LikeMapper.toLikeInformation(like)).collect(Collectors.toList());
    }
}
