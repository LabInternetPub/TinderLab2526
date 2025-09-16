package cat.tecnocampus.tinderlab2526.application.mappers;

import cat.tecnocampus.tinderlab2526.application.inputDTO.ProfileCommand;
import cat.tecnocampus.tinderlab2526.domain.Gender;
import cat.tecnocampus.tinderlab2526.domain.Passion;
import cat.tecnocampus.tinderlab2526.domain.Profile;

public class ProfileMapper {

    public static Profile inputProfileToDomain(ProfileCommand inputDTO) {
        Profile profile  = new Profile();
        profile.setEmail(inputDTO.email());
        profile.setNickname(inputDTO.nickname());
        profile.setGender(Gender.valueOf(inputDTO.gender()));
        profile.setAttraction(Gender.valueOf(inputDTO.attraction()));
        profile.setPassion(Passion.valueOf(inputDTO.passion()));

        return profile;
    }
}
