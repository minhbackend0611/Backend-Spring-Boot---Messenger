package com.example.spring_security.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignInRequest {
    @NotBlank(message = "username must not be blank")
    private String username;
    @NotBlank(message = "Password must not be blank")
    private String password;
}
