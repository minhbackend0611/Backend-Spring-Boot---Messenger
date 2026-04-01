package com.example.spring_security.entities;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "delete_group_conversation")
public class DeleteGroupConversation {
    @EmbeddedId
    private DeleteGroupConversationId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", insertable = false, updatable = false)
    private User member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_conversation_id", insertable = false, updatable = false)
    private GroupConversation groupConversation;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
