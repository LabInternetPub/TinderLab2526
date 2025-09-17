package cat.tecnocampus.tinderlab2526.integration;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Profile;

import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Profile("test")
public class TinderIntegrationTest {

    @LocalServerPort
    private int port;

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
                .statusCode(200)
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
                .statusCode(404)
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
                .statusCode(204);
    }

    @Test
    public void addLikesNotCompatible() {
        RestAssured
            .given()
                .contentType("application/json")
            .when()
                .post("/profiles/{originId}/likes/{targetId}", 1L, 2L)
            .then()
                .statusCode(409)
                .body("title", equalTo("Profiles are not compatible"));
    }

}
