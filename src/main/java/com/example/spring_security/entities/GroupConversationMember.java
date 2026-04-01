package com.example.spring_security.entities;

import com.example.spring_security.entities.Enum.GroupRole;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "group_conversation_member")
public class GroupConversationMember {
    @EmbeddedId
    private GroupConversationMemberId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", insertable = false, updatable = false)
    private User member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_conversation_id", insertable = false, updatable = false)
    private GroupConversation groupConversation;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    @Column(name = "appointed_at")
    private LocalDateTime appointedAt;

    @Column(name = "history_cleared_at")
    private LocalDateTime historyClearedAt;

    @Column(name = "group_role")
    private GroupRole groupRole;
}
