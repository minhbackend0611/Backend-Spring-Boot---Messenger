package com.example.spring_security.dto.request;

import com.example.spring_security.entities.Enum.Gender;
import com.example.spring_security.entities.Enum.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ManageUserRequest {
    @NotBlank(message = "This field must not be blank.")
    String username;
    String firstName;
    String lastName;
    @NotBlank(message = "This field must not be blank.")
    @Email(message = "This field must be a valid email.")
    String email;
    @NotBlank(message = "This field must not be blank.")
    String password;
    Gender gender;
    Role role;
    String address;
    String avatarUrl;
    LocalDate birthDay;
    Boolean isActive;
    Boolean isAccepted;
    Boolean isOnline;
    LocalDateTime joinedAt;
    LocalDateTime updatedAt;
}
