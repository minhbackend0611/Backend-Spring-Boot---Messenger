package com.example.spring_security.dto.request;

import com.example.spring_security.entities.Enum.Gender;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProfileRequest {

    @Email(message = "Email must be a valid email address")
    @Size(max = 255, message = "Your email exceeded 255 characters.")
    private String email;

    @Pattern(
            regexp = "^[a-zA-Z0-9]{6,30}$",
            message = "Username must be 6-30 characters long, contain only letters and digits, and must not contain spaces"
    )
    private String username;

    @Pattern(
            regexp = "^[a-zA-ZÀ-ỹà-ỹ\\s]{1,20}$",
            message = "First name must contain only letters (with optional accents) and spaces, and be 1-30 characters long"
    )
    private String firstName;
    @Pattern(
            regexp = "^[a-zA-ZÀ-ỹà-ỹ\\s]{1,20}$",
            message = "Last name must contain only letters (with optional accents) and spaces, and be 1-30 characters long"
    )
    private String lastName;


    private Gender gender;

    @Size(max = 250, message = "Address must be at most 250 characters")
    private String address;

    @Past(message = "Birthday must be a past date")
    private LocalDate birthday;

}
