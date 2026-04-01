package com.example.spring_security.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangePasswordRequest {

    private String oldPassword;

    @NotBlank(message = "Password must not be blank")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,30}$",
            message = "Password must be 8-30 characters long, include at least one uppercase letter, one lowercase letter, and one number and must not contain spaces"
    )
    private String newPassword;
    @NotBlank(message = "Confirm new password must not be blank")
    private String confirmPassword;
}
