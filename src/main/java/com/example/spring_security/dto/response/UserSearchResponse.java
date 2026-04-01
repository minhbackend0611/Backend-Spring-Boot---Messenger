package com.example.spring_security.dto.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserSearchResponse extends BaseUserResponse {

    private String status;

    private LocalDateTime sentAt;

    public UserSearchResponse(Long userId, String username, String firstName, String lastName, String avatarUrl,
                              String status, java.sql.Timestamp sentAt) {
        super(userId, username, firstName, lastName, avatarUrl);
        this.status = status;
        this.sentAt = sentAt != null ? sentAt.toLocalDateTime() : null;
    }

}
