package com.example.spring_security.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "delete_private_conversation")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeletePrivateConversation {

    @EmbeddedId
    private DeletePrivateConversationId id;

    @Column(name = "deleted_at", nullable = false)
    private LocalDateTime deletedAt;
}

