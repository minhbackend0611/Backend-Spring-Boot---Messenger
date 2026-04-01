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
@Table(name = "group_conversation")
public class GroupConversation {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "group_conversation_id")
    private Long groupConversationId;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "group_name")
    private String groupName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "preview_message_id")
    private GroupConversationMessage previewMessage;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "is_encrypted")
    private Boolean isEncrypted;
}
