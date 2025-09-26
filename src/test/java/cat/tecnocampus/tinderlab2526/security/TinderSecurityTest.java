package cat.tecnocampus.tinderlab2526.security;

import cat.tecnocampus.tinderlab2526.application.TinderService;
import cat.tecnocampus.tinderlab2526.application.outputDTO.ProfileInformation;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;

@SpringBootTest
@AutoConfigureMockMvc
public class TinderSecurityTest {


    @TestConfiguration
    static class MockConfig {
        @Bean
        public TinderService tinderService() {
            return Mockito.mock(TinderService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TinderService tinderService;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(mockMvc);
        Mockito.when(tinderService.getProfileById(1L))
                .thenReturn(Optional.of(new TestProfileInformationDTO(1L, "Alice", "alice@tecnocampu.cat", "Woman",
                        "Man", "Dance")));
    }

    @Test
    void helloWorld() throws Exception {
        RestAssuredMockMvc
                .when()
                    .get("/helloWorld")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(equalTo("Hello World"));
    }

    @Test
    void helloUserUnauthorized() throws Exception {
        RestAssuredMockMvc
                .when()
                    .get("/helloUser")
                .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @WithMockUser(username = "1", authorities = {"SCOPE_ADMIN", "SCOPE_USER"})
    void testProfilesMeHappyPath() throws Exception {
        RestAssuredMockMvc
            .given()
                .contentType("application/json")
            .when()
                .get("/profiles/me")
            .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(1))
                .body("nickname", equalTo("Alice"));
    }

    @Test
    @WithMockUser(username = "1", authorities = "SCOPE_ADMIN")
    void testProfilesMeNOAuthorized() throws Exception {
        RestAssuredMockMvc
                .when()
                    .get("/profiles/me")
                .then()
                    .statusCode(HttpStatus.FORBIDDEN.value());
    }



    private class TestProfileInformationDTO implements ProfileInformation {
        private Long id;
        private String nickname;
        private String email;
        private String gender;
        private String attraction;
        private String passion;

        public TestProfileInformationDTO(Long id, String nickname, String email, String gender, String attraction, String passion) {
            this.id = id;
            this.nickname = nickname;
            this.email = email;
            this.gender = gender;
            this.attraction = attraction;
            this.passion = passion;
        }

        @Override
        public Long getId() {
            return id;
        }

        @Override
        public String getNickname() {
            return nickname;
        }

        @Override
        public String getEmail() {
            return email;
        }

        @Override
        public String getGender() {
            return gender;
        }

        @Override
        public String getAttraction() {
            return attraction;
        }

        @Override
        public String getPassion() {
            return passion;
        }
    }
}



