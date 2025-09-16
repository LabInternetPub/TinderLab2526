package cat.tecnocampus.tinderlab2526.api;

import cat.tecnocampus.tinderlab2526.application.TinderService;
import cat.tecnocampus.tinderlab2526.application.exceptions.ProfileDoesNotExistException;
import cat.tecnocampus.tinderlab2526.application.inputDTO.ProfileCommand;
import cat.tecnocampus.tinderlab2526.application.outputDTO.ProfileInformation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
public class TinderRestController {
    private final TinderService tinderService;

    public TinderRestController(TinderService tinderService) {
        this.tinderService = tinderService;
    }

    @PostMapping("/profiles")
    public ResponseEntity<Void> createProfile(@RequestBody ProfileCommand profileCommand, UriComponentsBuilder uriBuilder) {
        Long id = tinderService.createProfile(profileCommand); // Assume this returns the id
        var location = uriBuilder.path("/profiles/{id}").buildAndExpand(id).toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/profiles/{id}")
    public ProfileInformation getProfile(@PathVariable Long id) {
        ProfileInformation profileInformation = tinderService.getProfileById(id).orElseThrow(() -> new ProfileDoesNotExistException("Profile with id " + id + " does not exist"));
        return profileInformation;
    }


}
