package cat.tecnocampus.tinderlab2526.api;

import cat.tecnocampus.tinderlab2526.application.TinderService;
import cat.tecnocampus.tinderlab2526.application.exceptions.ProfileDoesNotExistException;
import cat.tecnocampus.tinderlab2526.application.inputDTO.ProfileCommand;
import cat.tecnocampus.tinderlab2526.application.outputDTO.ProfileInformation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
public class TinderRestController {
    private final TinderService tinderService;

    public TinderRestController(TinderService tinderService) {
        this.tinderService = tinderService;
    }

    @PostMapping("/profiles")
    public ResponseEntity<Void> createProfile(@RequestBody @Valid ProfileCommand profileCommand, UriComponentsBuilder uriBuilder) {
        Long id = tinderService.createProfile(profileCommand); // Assume this returns the id
        var location = uriBuilder.path("/profiles/{id}").buildAndExpand(id).toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/profiles/{id}")
    public ProfileInformation getProfile(@PathVariable Long id) {
        ProfileInformation profileInformation = tinderService.getProfileById(id).orElseThrow(() -> new ProfileDoesNotExistException("Profile with id " + id + " does not exist"));
        return profileInformation;
    }

    @PostMapping("/profiles/{originId}/likes/{targetId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addLike(@PathVariable Long originId, @PathVariable Long targetId) {
        tinderService.addLike(originId, targetId);
    }

    @GetMapping("/profiles/{id}/candidates")
    public List<ProfileInformation> getCandidates(@PathVariable Long id) {
        return tinderService.getCandidates(id);
    }

}
