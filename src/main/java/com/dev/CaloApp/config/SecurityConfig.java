package com.dev.CaloApp.config;

import com.dev.CaloApp.repository.UserRepository;
import com.dev.CaloApp.service.AuthenticationService;
import com.dev.CaloApp.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${jwt.signer-key}")
    private String signerKey;

    @Autowired
    UserService userService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserRepository userRepository;
    private static final String [] PUBLIC_ENDPOINTS = {
            "/users/create",          // Cho phép user tự đăng ký
            "user/oauth2/**",         // Cho phép toàn bộ OAuth2 endpoint
            "/auth", // Cho phép đăng nhập
            "/api/gemini/generate",
            "/api/password/**",
            "/auth/google"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Tắt CSRF vì dùng JWT
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Không dùng session

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll() // Các API công khai, không yêu cầu token
//                        .requestMatchers("/admin/**").hasRole("ADMIN") // Chỉ ADMIN được truy cập
//                        .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN") // USER và ADMIN đều truy cập được
                        .anyRequest().authenticated() // Mặc định yêu cầu xác thực cho tất cả request còn lại
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder())
                        )
                )

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            System.out.println("⚠️ Authentication error: " + authException.getMessage());
                            System.out.println("⚠️ Request URI: " + request.getRequestURI());
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write("Unauthorized: Token is missing or invalid!");
                        })
                );

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        SecretKey secretKey = new SecretKeySpec(signerKey.getBytes(), "HmacSHA512");

        return NimbusJwtDecoder
                .withSecretKey(secretKey)
                .macAlgorithm(org.springframework.security.oauth2.jose.jws.MacAlgorithm.HS512)
                .build();
    }

    private OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler(ClientRegistrationRepository clientRegistrationRepository) {
        OidcClientInitiatedLogoutSuccessHandler successHandler = new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
        successHandler.setPostLogoutRedirectUri("http://localhost:8080");
        return successHandler;
    }
}
