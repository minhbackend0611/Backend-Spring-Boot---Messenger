package com.example.spring_security.dto.response;

import com.example.spring_security.entities.User;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class FriendRequestResponse extends BaseUserResponse {
    private LocalDateTime sentAt;

    public FriendRequestResponse(User user, LocalDateTime sentAt) {
        super(user.getUserId(), user.getUsername(), user.getFirstName(), user.getLastName(), user.getAvatarUrl());
        this.sentAt = sentAt;
    }

}
