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
@Table(name = "delete_group_conversation_message")
public class DeleteGroupConversationMessage {
    @EmbeddedId
    private DeleteGroupConversationMessageId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", insertable = false, updatable = false)
    private User member;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "is_all")
    private Boolean isAll;
}
