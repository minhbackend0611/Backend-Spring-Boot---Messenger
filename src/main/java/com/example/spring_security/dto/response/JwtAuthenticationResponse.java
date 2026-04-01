package com.example.spring_security.dto.response;

import com.example.spring_security.entities.User;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtAuthenticationResponse {
    private String token;

    private String refreshToken;

    private UserProfileResponse userProfileResponse;
}
