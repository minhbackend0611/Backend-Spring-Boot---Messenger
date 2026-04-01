package com.example.spring_security.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import jakarta.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpRequest {

    @NotBlank(message = "First name must not be blank")
    @Pattern(
            regexp = "^[a-zA-ZÀ-ỹà-ỹ\\s]{1,20}$",
            message = "First name must contain only letters (with optional accents) and spaces, and be 1-30 characters long"
    )
    private String firstName;

    @NotBlank(message = "Last name must not be blank")
    @Pattern(
            regexp = "^[a-zA-ZÀ-ỹà-ỹ\\s]{1,20}$",
            message = "Last name must contain only letters (with optional accents) and spaces, and be 1-30 characters long"
    )
    private String lastName;

    @NotBlank(message = "Username must not be blank")
    @Pattern(
            regexp = "^[a-zA-Z0-9]{6,30}$",
            message = "Username must be 6-30 characters long, contain only letters and digits, and must not contain spaces"
    )
    private String username;

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email must be a valid email address")
    @Size(max = 255, message = "Your email exceeded 255 characters.")
    private String email;

    @NotBlank(message = "Password must not be blank")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,30}$",
            message = "Password must be 8-30 characters long, include at least one uppercase letter, one lowercase letter, and one number and must not contain spaces"
    )
    private String password;
    @NotBlank(message = "Confirm password must not be blank")
    private String confirmPassword;
}

