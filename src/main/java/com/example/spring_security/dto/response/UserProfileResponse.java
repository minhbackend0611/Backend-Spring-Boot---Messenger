package com.example.spring_security.dto.response;

import com.example.spring_security.entities.Enum.Gender;
import com.example.spring_security.entities.Enum.Role;
import com.example.spring_security.entities.User;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserProfileResponse extends BaseUserResponse {
    private String address;
    private LocalDate birthDay;
    private Gender gender;
    private LocalDateTime joinedAt;
    private Role role;
    private String email;

    public UserProfileResponse(User user) {
        super(user.getUserId(), user.getUsername(), user.getFirstName(), user.getLastName(), user.getAvatarUrl());

        this.address = user.getAddress();

        this.birthDay = user.getBirthday();

        this.joinedAt = user.getJoinedAt();

        this.gender = user.getGender();

        this.role = user.getRole();

        this.email = user.getEmail();
    }

}
