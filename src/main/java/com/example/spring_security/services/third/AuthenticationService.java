package com.example.spring_security.services.third;

import com.example.spring_security.dto.request.RefreshTokenRequest;
import com.example.spring_security.dto.request.SignInRequest;
import com.example.spring_security.dto.request.SignUpRequest;
import com.example.spring_security.dto.request.VerificationRequest;
import com.example.spring_security.dto.response.JwtAuthenticationResponse;
import com.example.spring_security.entities.User;

import java.util.Map;

public interface AuthenticationService {
    User signup(SignUpRequest signUpRequest);

    JwtAuthenticationResponse signin(SignInRequest signInRequest);

    JwtAuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest);

    Map<String, String> createVerificationToken(VerificationRequest verificationRequest);

    User verifyToken (String token);

    Map<String, String> resetPassword(VerificationRequest verificationRequest);

}
