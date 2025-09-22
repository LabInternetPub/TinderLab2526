package cat.tecnocampus.tinderlab2526.security.authentication;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthenticationResponse {

  @JsonProperty("access_token")
  private String accessToken;

  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }
}
