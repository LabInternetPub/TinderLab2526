package cat.tecnocampus.tinderlab2526.security.authentication;

import cat.tecnocampus.tinderlab2526.application.exceptions.ProfileDoesNotExistException;
import cat.tecnocampus.tinderlab2526.domain.Profile;
import cat.tecnocampus.tinderlab2526.persistence.ProfileRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class ProfileDetailsService implements UserDetailsService {
    private ProfileRepository profileRepository;

    public ProfileDetailsService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws ProfileDoesNotExistException {
        Profile profile = profileRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new ProfileDoesNotExistException("Profile not Found with nickname: " + userId));

        return new ProfileDetails(profile);
    }

}