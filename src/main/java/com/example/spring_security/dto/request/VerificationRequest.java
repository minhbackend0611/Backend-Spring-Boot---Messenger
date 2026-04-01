package com.example.spring_security.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationRequest {
    @NotBlank(message = "Email must not be blank.")
    @Email(message = "This field must be email")
    private String email;
}
