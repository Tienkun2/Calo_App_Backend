package com.dev.CaloApp.service;

import com.dev.CaloApp.dto.request.AuthenticationRequest;
import com.dev.CaloApp.dto.request.InstrospectRequest;
import com.dev.CaloApp.dto.response.AuthenticationResponse;
import com.dev.CaloApp.dto.response.IntrospectResponse;
import com.dev.CaloApp.entity.User;
import com.dev.CaloApp.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.XSlf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.threeten.bp.temporal.ChronoUnit;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationService {

    @Autowired
    UserRepository userRepository;

    @Value("${jwt.signer-key}")
    private String signerKey;


    public IntrospectResponse introspect(InstrospectRequest request) throws JOSEException, ParseException {
        // lấy token request gửi đến
        var token = request.getToken();

        // Tạo JWSVerifier để xác minh token
        JWSVerifier verifier = new MACVerifier(signerKey.getBytes());

        // Giải mã Token và lấy Thời gian hết hạn
        // SignedJWT.parse(token): Giải mã JWT để lấy thông tin trong payload.
        //signedJWT.getJWTClaimsSet().getExpirationTime(): Lấy thời gian hết hạn (exp) của token.
        SignedJWT signedJWT = SignedJWT.parse(token);
        Date expityTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        //Xác minh Token hợp lệ
        // Kiểm tra token có bị giả mạo không bằng cách xác minh chữ ký với secret key.
        //Nếu chữ ký hợp lệ → verified = true, ngược lại false.
        var verified = signedJWT.verify(verifier);

        return IntrospectResponse.builder()
                .valid(verified && expityTime.after(new Date()))
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);

        // Luôn thực hiện hash mật khẩu để tránh timing attack
        String dummyPassword = "$2a$10$dummyhashdummyhashdummyhashdummyha"; // Hash giả

        var userOpt = userRepository.findByEmail(request.getEmail());
        boolean authenticated = false;

        if (userOpt.isPresent()) {
            authenticated = encoder.matches(request.getPassword(), userOpt.get().getPassword());
        } else {
            // Nếu email không tồn tại, vẫn thực hiện hash mật khẩu giả để tránh lộ thông tin
            encoder.matches(request.getPassword(), dummyPassword);
        }

        if (!authenticated) {
            return AuthenticationResponse.builder()
                    .authenticated(false)
                    .build();
        }

        // Nếu thành công, tạo token
        String token = generateToken(userOpt.get());

        return AuthenticationResponse.builder()
                .authenticated(true)
                .token(token)
                .build();
    }

    // Có nhiệm vụ tạo jwt(json web token)
    public String generateToken(User user){
        String userId = (user.getId() != null) ? String.valueOf(user.getId()) : user.getGoogleId();
        // Phần header, xác định thuật toán ký
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        // ClaimSet
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(userId) // Lưu userId
                .issuer("dev.CaloApp") // Nơi cấp token
                .issueTime(new Date()) // Thời gian tạo token
                .expirationTime(Date.from(Instant.now().plus(Duration.ofHours(2)))) // Hết hạn sau 2 giờ
                .claim("email", user.getEmail()) // Lưu email
//                .claim("roles", user.getRoles()) // Lưu quyền hạn (USER, ADMIN)
                .build();
        // Tạo payload
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        // Tạo token: header + payload
        JWSObject jwsObject = new JWSObject(header,payload);
        // Kí token : Dùng HMAC SHA-512 với SIGNER_KEY để ký token.
        //Nếu thành công, token sẽ được mã hóa & trả về
        try{
            jwsObject.sign(new MACSigner(signerKey.getBytes()));
            return jwsObject.serialize();
        }catch (JOSEException exception){
            log.error("Cannot create token");
            throw new RuntimeException(exception);
        }

    }
}
