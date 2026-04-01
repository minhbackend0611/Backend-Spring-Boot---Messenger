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
@Table(name = "private_conversation", uniqueConstraints = {
                @UniqueConstraint(columnNames = { "user1_id", "user2_id" })
})
public class PrivateConversation {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "private_conversation_id")
        private Long privateConversationId;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user1_id", nullable = false)
        private User user1;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user2_id", nullable = false)
        private User user2;

        @Column(name = "created_at", nullable = false)
        private LocalDateTime createdAt;

        @Column(name = "user1_cleared_at")
        private LocalDateTime user1ClearedAt;

        @Column(name = "user2_cleared_at")
        private LocalDateTime user2ClearedAt;

        @OneToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "preview_message_id", nullable = true)
        private PrivateConversationMessage previewMessage;
}
