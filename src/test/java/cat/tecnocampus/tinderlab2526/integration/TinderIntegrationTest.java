package cat.tecnocampus.tinderlab2526.integration;

import cat.tecnocampus.tinderlab2526.domain.ProfilesMotherTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(statements = {"DELETE FROM like_profile", "DELETE FROM profile", "ALTER TABLE profile ALTER COLUMN id RESTART WITH 1"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:data-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class TinderIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private cat.tecnocampus.tinderlab2526.persistence.ProfileRepository profileRepository;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    void getExistingProfile() {
        RestAssured
                .given()
                .when()
                .get("/profiles/1")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(1))
                .body("nickname", equalTo("Alice"));
    }

    @Test
    void getNonExistingProfile() {
        RestAssured
            .given()
            .when()
                .get("/profiles/999")
            .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("title", equalTo("Profile Not Found"));
    }

    @Test
    public void addLikesCompatible() {
        RestAssured
            .given()
                .contentType("application/json")
            .when()
                .post("/profiles/{originId}/likes/{targetId}", 2L, 3L)
            .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        profileRepository.findByIdWithLikes(2L).ifPresent(origin -> {
            assertEquals(1, origin.getLikes().size());
        });
    }

    @Test
    public void addLikesNotCompatible() {
        RestAssured
            .given()
                .contentType("application/json")
            .when()
                .post("/profiles/{originId}/likes/{targetId}", 1L, 2L)
            .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .body("title", equalTo("Profiles are not compatible"));
    }

    @Test
    public void postCorrectProfile() {
        cat.tecnocampus.tinderlab2526.domain.Profile man = ProfilesMotherTest.ManAttractedByWomanPassionMusicProfiles(null);
        var response = RestAssured
                .given()
                .contentType("application/json")
                .body(man)
                .when()
                .post("/profiles")

                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body(equalTo(""))
                .header("Location", matchesRegex(".*profiles\\/\\d+$"))
                .extract().response();

        String location = response.getHeader("Location");
        Long id = Long.valueOf(location.substring(location.lastIndexOf('/') + 1));
        assertEquals("ManAttractedByManPassionMusic", profileRepository.findById(id).orElseThrow().getNickname());
    }

    @Test
    public void postIncorrectProfile() {
        Map<String, Object> incorrectProfile = new HashMap<>();
        incorrectProfile.put("id", null);
        incorrectProfile.put("email", "invalid-email");
        incorrectProfile.put("nickname", "bob");
        incorrectProfile.put("gender", "hello");
        incorrectProfile.put("attraction", "bye");
        incorrectProfile.put("passion", "look at the sky");

        RestAssured
                .given()
                .contentType("application/json")
                .body(incorrectProfile)
                .when()
                .post("/profiles")

                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("title", equalTo("Bad request"))
                .body("status", equalTo(400))
                .body("detail", allOf(containsString("nickname"), containsString("email"), containsString("gender"),
                        containsString("attraction"), containsString("passion")));
    }

    @Test
    public void getCandidates() {
        RestAssured
            .given()
            .when()
                .get("/profiles/{id}/candidates", 3L)
            .then()
                .statusCode(HttpStatus.OK.value())
                .body("size()", is(2))
                .body("id", hasItems(2, 4));
    }

    @Test
    public void getCandidatesNotExistingProfile() {
        RestAssured
            .given()
            .when()
                .get("/profiles/{id}/candidates", 999L)
            .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("title", equalTo("Profile Not Found"));
    }

}
