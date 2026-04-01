package com.example.spring_security.dto.response;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserFriendResponse extends BaseUserResponse {
    private Boolean isOnline;
    private String address;
    private LocalDateTime madeFriendAt;

    public UserFriendResponse(Long userId, String username, String firstName, String lastName, String avatarUrl,
                              Boolean isOnline, String address, java.sql.Timestamp madeFriendAt) {
        super(userId, username, firstName, lastName, avatarUrl);
        this.isOnline =  isOnline;
        this.address = address;
        this.madeFriendAt = madeFriendAt != null ? madeFriendAt.toLocalDateTime() : null;
    }
}

