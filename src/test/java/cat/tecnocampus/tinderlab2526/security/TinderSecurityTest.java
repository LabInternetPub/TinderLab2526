package cat.tecnocampus.tinderlab2526.security;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TinderSecurityTest {

    @Autowired
    private MockMvc mockMvc;


    @Test
    void helloWorld() throws Exception {
        mockMvc.perform(get("/helloWorld"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello World"));
    }

   @Test
   @WithMockUser(username = "1", authorities = {"SCOPE_ADMIN", "SCOPE_USER"})
    void endPoint() throws Exception {
        mockMvc.perform(get("/profiles/me"))
                .andExpect(status().isOk());
    }

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @Test
    @WithMockUser(username = "1", authorities = {"SCOPE_ADMIN", "SCOPE_USER"})
    void testEndpoint() {
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

}



