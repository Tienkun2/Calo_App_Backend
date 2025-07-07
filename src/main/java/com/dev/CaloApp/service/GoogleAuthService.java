package com.dev.CaloApp.service;

import com.dev.CaloApp.dto.request.ApiResponse;
import com.dev.CaloApp.dto.request.GoogleLoginRequest;
import com.dev.CaloApp.dto.response.GoogleLoginResponse;
import com.dev.CaloApp.dto.response.UserLoginGoogleResponse;
import com.dev.CaloApp.entity.User;
import com.dev.CaloApp.repository.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class GoogleAuthService {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthenticationService authenticationService;

    public ApiResponse<GoogleLoginResponse> authenticateWithGoogle(GoogleLoginRequest request) {
        try {
            String idToken = request.getIdToken();
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken googleIdToken = verifier.verify(idToken);
            System.out.println("Received ID Token: " + request.getIdToken());
            if (googleIdToken == null) {
                return ApiResponse.<GoogleLoginResponse>builder()
                        .code(400)
                        .message("ID token không hợp lệ")
                        .build();
            }

            GoogleIdToken.Payload payload = googleIdToken.getPayload();
            String googleId = payload.getSubject();
            String email = payload.getEmail();
            String name = (String) payload.get("name");
            String picture = (String) payload.get("picture");

            Optional<User> existingUser = userRepository.findByEmail(email);
            User user = existingUser.orElseGet(() -> {
                User newUser = new User();
                newUser.setGoogleId(googleId);
                newUser.setEmail(email);
                newUser.setName(name);
                newUser.setPassword("");
                newUser.setGoal("");
                newUser.setAge(0);
                newUser.setGender("");
                newUser.setWeight(0);
                newUser.setHeight(0);
                return userRepository.save(newUser);
            });

            if (existingUser.isPresent()) {
                user.setGoogleId(googleId);
                if (user.getName() == null || user.getName().isEmpty()) {
                    user.setName(name);
                }
                userRepository.save(user);
            }

            String jwtToken = authenticationService.generateToken(user);

            GoogleLoginResponse responseData = GoogleLoginResponse.builder()
                    .token(jwtToken)
                    .user(UserLoginGoogleResponse.builder()
                            .id(user.getId())
                            .email(user.getEmail())
                            .name(user.getName())
                            .picture(picture)
                            .build())
                    .build();

            return ApiResponse.<GoogleLoginResponse>builder()
                    .code(200)
                    .message("Đăng nhập thành công")
                    .result(responseData)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.<GoogleLoginResponse>builder()
                    .code(500)
                    .message("Lỗi khi xác thực: " + e.getMessage())
                    .build();
        }
    }

}
