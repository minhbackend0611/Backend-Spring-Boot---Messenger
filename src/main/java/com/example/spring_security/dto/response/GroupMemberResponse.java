package com.example.spring_security.dto.response;

import com.example.spring_security.entities.Enum.GroupRole;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class GroupMemberResponse extends BaseUserResponse {

    private Boolean isOnline;

    private GroupRole groupRole;

    private LocalDateTime joinedAt;

    private LocalDateTime appointedAt;

    GroupMemberResponse(Long userId, String username, String firstName, String lastName, String avatarUrl, Boolean isOnline,
                        Short groupRole, java.sql.Timestamp joinedAt, java.sql.Timestamp appointedAt) {
        super(userId, username, firstName, lastName, avatarUrl);
        this.isOnline = isOnline;
        GroupRole.Converter converter = new GroupRole.Converter();
        this.groupRole = converter.fromShort(groupRole);
        this.joinedAt = joinedAt != null ? joinedAt.toLocalDateTime() : null;
        this.appointedAt = appointedAt != null ? appointedAt.toLocalDateTime() : null;
    }

}
