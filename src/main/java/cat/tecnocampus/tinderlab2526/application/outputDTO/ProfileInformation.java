package cat.tecnocampus.tinderlab2526.application.outputDTO;

public interface ProfileInformation {
    Long getId();
    String getEmail();
    String getNickname();
    GenderDTO getGenderDTO();
    GenderDTO getAttractionDTO();
    PassionDTO getPassionDTO();
}
