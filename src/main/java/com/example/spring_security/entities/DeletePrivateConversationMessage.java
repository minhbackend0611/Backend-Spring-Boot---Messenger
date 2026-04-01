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
@Table(name = "delete_private_conversation_message")
public class DeletePrivateConversationMessage {

    @EmbeddedId
    private DeletePrivateConversationMessageId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "is_all")
    private Boolean isAll;
}
