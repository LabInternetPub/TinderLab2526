package cat.tecnocampus.tinderlab2526.domain;

public class ProfilesMotherTest {

    public static Profile ManAttractedByWomanPassionMusicProfiles(Long id) {
        Profile profile = new Profile();
        profile.setId(id);
        profile.setNickname("ManAttractedByManPassionMusic" + id);
        profile.setEmail("ManAttractedByManPassionMusic" + id + "@email.com");
        profile.setGender(Gender.Man);
        profile.setAttraction(Gender.Woman);
        profile.setPassion(Passion.Music);
        return profile;
    }

    public static Profile ManAttractedByWomanPassionDanceProfiles(Long id) {
        Profile profile = new Profile();
        profile.setId(id);
        profile.setNickname("ManAttractedByManPassionMusic" + id);
        profile.setEmail("ManAttractedByManPassionMusic" + id + "@email.com");
        profile.setGender(Gender.Man);
        profile.setAttraction(Gender.Woman);
        profile.setPassion(Passion.Dance);
        return profile;
    }

    public static Profile WomanAttractedByManPassionMusicProfiles(Long id) {
        Profile profile = new Profile();
        profile.setId(id);
        profile.setNickname("WomanAttractedByManPassionMusic" + id);
        profile.setEmail("WomanAttractedByManPassionMusic" + id + "@email.com");
        profile.setGender(Gender.Woman);
        profile.setAttraction(Gender.Man);
        profile.setPassion(Passion.Music);
        return profile;
    }

    public static Profile WomanAttractedByManPassionDanceProfiles(Long id) {
        Profile profile = new Profile();
        profile.setId(id);
        profile.setNickname("WomanAttractedByManPassionMusic" + id);
        profile.setEmail("WomanAttractedByManPassionMusic" + id + "@email.com");
        profile.setGender(Gender.Woman);
        profile.setAttraction(Gender.Man);
        profile.setPassion(Passion.Dance);
        return profile;
    }

    public static Profile WomanAttractedByBisexualPassionMusicProfiles(Long id) {
        Profile profile = new Profile();
        profile.setId(id);
        profile.setNickname("WomanAttractedByManPassionMusic" + id);
        profile.setEmail("WomanAttractedByManPassionMusic" + id + "@email.com");
        profile.setGender(Gender.Woman);
        profile.setAttraction(Gender.Bisexual);
        profile.setPassion(Passion.Music);
        return profile;
    }

    public static Profile WomanAttractedByBisexualPassionDanceProfiles(Long id) {
        Profile profile = new Profile();
        profile.setId(id);
        profile.setNickname("WomanAttractedByManPassionMusic" + id);
        profile.setEmail("WomanAttractedByManPassionMusic" + id + "@email.com");
        profile.setGender(Gender.Woman);
        profile.setAttraction(Gender.Bisexual);
        profile.setPassion(Passion.Dance);
        return profile;
    }

}
