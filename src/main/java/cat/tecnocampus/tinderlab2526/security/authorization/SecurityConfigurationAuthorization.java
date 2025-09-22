package cat.tecnocampus.tinderlab2526.security.authorization;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.oauth2.core.authorization.OAuth2AuthorizationManagers.hasAnyScope;
import static org.springframework.security.oauth2.core.authorization.OAuth2AuthorizationManagers.hasScope;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfigurationAuthorization {
    private final JwtDecoder jwtDecoder;
    private static final String[] WHITE_LIST_URL = {
            "/loginJWT",
            "/h2-console/**",
            "/webjars/**",
            "/v3/api-docs/**", //this is for swagger
            "/swagger-ui/**",
            "/swagger-ui.html"};

    public SecurityConfigurationAuthorization(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http
                .cors(cors -> cors.disable())
                .csrf(csrf -> csrf.disable()) //This is to disable the csrf protection. It is not needed for this project since the application is stateless (and we are using JWT)
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions
                        .sameOrigin()))   // This is to allow the h2-console to be used in the browser. It allows the browser to render the response in a frame.
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(WHITE_LIST_URL).permitAll();
                    auth.requestMatchers("/helloWorld").permitAll();
                    auth.requestMatchers("/helloUser").access(hasScope("USER"));//.hasRole("USER");
                    auth.requestMatchers("/helloAdmin").access(hasScope("ADMIN"));
                    auth.requestMatchers("/helloUserAdmin").access(hasAnyScope("USER", "ADMIN"));
                    auth.requestMatchers(HttpMethod.POST, "/profiles").permitAll();
                    auth.requestMatchers("/profiles/me/**").authenticated();
                    auth.requestMatchers("/profiles/**").access(hasScope("ADMIN"));
                    auth.anyRequest().authenticated();
                })
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer((oauth2) -> oauth2.jwt((jwt) -> jwt.decoder(jwtDecoder)))
                .httpBasic(Customizer.withDefaults())
                .build();
    }
}
