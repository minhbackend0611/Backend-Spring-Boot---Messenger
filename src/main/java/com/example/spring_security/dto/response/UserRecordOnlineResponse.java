package com.example.spring_security.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserRecordOnlineResponse extends BaseUserResponse {

    private Boolean isOnline;

    private LocalDateTime joinedAt;

    private Long activityCount;

    private LocalDateTime lastOnlineAt;

    private Long totalOnlineSeconds;

    public UserRecordOnlineResponse(Long userId, String username, String firstName,
                                    String lastName, String avatarUrl, Boolean isOnline, java.sql.Timestamp joinedAt, Long activityCount,
                                    java.sql.Timestamp lastOnlineAt, Long totalOnlineSeconds) {

        super(userId, username, firstName, lastName, avatarUrl);

        this.isOnline = isOnline;

        this.joinedAt = joinedAt != null ? joinedAt.toLocalDateTime() : null;

        this.activityCount = activityCount;

        this.lastOnlineAt = lastOnlineAt != null ? lastOnlineAt.toLocalDateTime() : null;

        this.totalOnlineSeconds = totalOnlineSeconds;

    }

}
