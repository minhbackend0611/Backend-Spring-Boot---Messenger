package com.example.spring_security.services.third.impl;

import com.example.spring_security.dto.request.RefreshTokenRequest;
import com.example.spring_security.dto.request.SignInRequest;
import com.example.spring_security.dto.request.SignUpRequest;
import com.example.spring_security.dto.request.VerificationRequest;
import com.example.spring_security.dto.response.JwtAuthenticationResponse;
import com.example.spring_security.dto.response.UserProfileResponse;
import com.example.spring_security.entities.Enum.Gender;
import com.example.spring_security.entities.RecordSignIn;
import com.example.spring_security.entities.Token.RequestPasswordReset;
import com.example.spring_security.entities.Enum.Role;
import com.example.spring_security.entities.Token.VerifyToken;
import com.example.spring_security.exception.CustomException;
import com.example.spring_security.repository.RecordSignInRepository;
import com.example.spring_security.repository.TokenRepo.RequestPasswordResetRepository;
import com.example.spring_security.repository.UserRepository;
import com.example.spring_security.repository.TokenRepo.VerifyTokenRepository;
import com.example.spring_security.services.third.AuthenticationService;
import com.example.spring_security.services.third.EmailService;
import com.example.spring_security.services.third.JWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.spring_security.entities.User;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JWTService jwtService;

    private final VerifyTokenRepository verifyTokenRepository;

    private final int resendVerificationDelay = 10; // minutes

    private final int VerificationTokenExpiredMins = 10;

    private final EmailService emailService;

    private final RequestPasswordResetRepository requestPasswordResetRepository;

    private final RecordSignInRepository recordSignInRepository;

    @Value("${app.base-url}")
    private String baseUrl;

    public User signup(SignUpRequest signUpRequest) {

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new RuntimeException("Username is already in use.");
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new RuntimeException("Email is already in use.");
        }

        if (!signUpRequest.getConfirmPassword().equals(signUpRequest.getPassword()))
            throw new RuntimeException("Passwords do not match.");

        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setFirstName(signUpRequest.getFirstName());
        user.setLastName(signUpRequest.getLastName());
        user.setRole(Role.USER);
        user.setHashPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setIsActive(true);
        user.setIsAccepted(true);
        user.setJoinedAt(LocalDateTime.now());
        user.setGender(Gender.HIDDEN);
        user.setAvatarUrl("");
        user.setAddress("");
        user.setIsOnline(false);
        user.setFriendCount(0);
        return userRepository.save(user);
    }

    public JwtAuthenticationResponse signin(SignInRequest signInRequest) {

        RecordSignIn recordSignIn = new RecordSignIn();
        recordSignIn.setSignedInAt(LocalDateTime.now());

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInRequest.getUsername(), signInRequest.getPassword()));

            var user = userRepository.findByUsername(signInRequest.getUsername())
                    .or(() -> userRepository.findByEmail(signInRequest.getUsername()))
                    .orElseThrow(() -> new IllegalArgumentException("User no longer exists."));

            var token = jwtService.generateToken(user);
            var refreshToken = jwtService.generateRefreshToken(new HashMap<>(), user);

            JwtAuthenticationResponse jwtAuthenticationResponse = new JwtAuthenticationResponse();

            jwtAuthenticationResponse.setToken(token);
            jwtAuthenticationResponse.setRefreshToken(refreshToken);
            jwtAuthenticationResponse.setUserProfileResponse(
                    new UserProfileResponse(user)
            );
            recordSignIn.setIsSuccessful(true);
            recordSignIn.setUser(user);
            recordSignInRepository.save(recordSignIn);
            return jwtAuthenticationResponse;
        } catch (RuntimeException e) {
            recordSignIn.setIsSuccessful(false);
            var user = userRepository.findByUsername(signInRequest.getUsername())
                    .or(() -> userRepository.findByEmail(signInRequest.getUsername()))
                    .orElse(null);
            recordSignIn.setUser(user);
            recordSignInRepository.save(recordSignIn);
            throw e;
        }
    }

    public JwtAuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String username = jwtService.extractUsername(refreshTokenRequest.getRefreshToken());
        User user = userRepository.findByUsername(username).orElseThrow();
        if (jwtService.isTokenValid(refreshTokenRequest.getRefreshToken(), user)) {
            var token = jwtService.generateToken(user);

            JwtAuthenticationResponse jwtAuthenticationResponse = new JwtAuthenticationResponse();

            jwtAuthenticationResponse.setToken(token);
            jwtAuthenticationResponse.setRefreshToken(refreshTokenRequest.getRefreshToken());
            return jwtAuthenticationResponse;
        }
        return null;
    }


    public Map<String, String> createVerificationToken(VerificationRequest verificationRequest) {

        User user = userRepository.findByEmail(verificationRequest.getEmail())
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "User no longer exists by email."));

        VerifyToken token = verifyTokenRepository.findByUser(user)
                .orElse(new VerifyToken());

        LocalDateTime now = LocalDateTime.now();
        if(token.getId() != null && token.getCreatedAt() != null &&
                token.getCreatedAt().plusMinutes(resendVerificationDelay).isAfter(now)) {
            throw new RuntimeException("Please wait before requesting another verification token.");
        }

        String newToken = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        token.setToken(newToken);
        token.setUser(user);
        token.setExpiredAt(now.plusMinutes(VerificationTokenExpiredMins));
        token.setCreatedAt(now);

        verifyTokenRepository.save(token);

        emailService.sendEmail(
                user.getEmail(),
                "Verify your account",
                "Hi " + user.getUsername() + ",\n\n" +
                        "Please use the verification token below to verify your email:\n" +
                        newToken + "\n\n" +
                        "This token will expire in 10 minutes."
        );
        Map<String, String> response = new HashMap<>();
        response.put("message", "Please check your email to get the verification token.");
        return response;
    }


    public User verifyToken (String token) {
        VerifyToken verifytoken = verifyTokenRepository.findByToken(token).orElseThrow(() -> new RuntimeException("Invalid token."));

        if (verifytoken.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token is already expired");
        }

        User user = verifytoken.getUser();

        user.setIsActive(true);

        verifyTokenRepository.delete(verifytoken);
        return userRepository.save(user);
    }

    public Map<String, String> resetPassword(VerificationRequest verificationRequest) {

        User user = userRepository.findByEmail(verificationRequest.getEmail())
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "User no longer exists by email."));

        RequestPasswordReset requestpasswordreset = requestPasswordResetRepository.findByUser(user).orElse(new RequestPasswordReset());

        LocalDateTime now = LocalDateTime.now();
        if(requestpasswordreset.getId() != null && requestpasswordreset.getCreatedAt() != null &&
                requestpasswordreset.getCreatedAt().plusSeconds(30).isAfter(now)) {
            throw new RuntimeException("Please wait for 30 seconds before requesting another password reset.");
        }
        String generatedPassword = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        user.setHashPassword(passwordEncoder.encode(generatedPassword));

        requestpasswordreset.setCreatedAt(now);
        requestpasswordreset.setToken(generatedPassword);
        requestpasswordreset.setUser(user);
        userRepository.save(user);
        requestPasswordResetRepository.save(requestpasswordreset);

        emailService.sendEmail(
                user.getEmail(),
                "Your New Password",
                   "Hi " + user.getUsername() + ",\n\n" +
                        "We’ve reset your password as requested.\n\n" +
                        "Your new temporary password is: " + generatedPassword + "\n\n" +
                        "Please log in using this password and change it immediately for security reasons.\n\n" +
                        "Best regards,\n" +
                        "Your Support Team"
        );

        Map<String, String> response = new HashMap<>();
        response.put("message", "Reset password successfully.");
        return response;
    }
}
