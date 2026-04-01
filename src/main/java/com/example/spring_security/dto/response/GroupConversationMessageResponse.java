package com.example.spring_security.dto.response;

import com.example.spring_security.entities.Enum.MessageType;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
// ❌ Do NOT add @AllArgsConstructor here, it conflicts with the manual
// constructor below
public class GroupConversationMessageResponse {

    private Long groupConversationId;
    private Long senderId;
    private String senderName;
    private String senderAvatar;
    private Long groupConversationMessageId;
    private String content;
    private LocalDateTime sentAt;
    private LocalDateTime updatedAt;
    private MessageType type;

    // ✅ MANUAL CONSTRUCTOR (Must match JPQL Query types exactly)
    public GroupConversationMessageResponse(
            Long groupConversationId,
            Long senderId,
            String senderName,
            String senderAvatar,
            Long groupConversationMessageId,
            String content,
            LocalDateTime sentAt, // Matches gcm.sentAt
            LocalDateTime updatedAt, // Matches gcm.updatedAt
            MessageType type // Matches gcm.type (Enum)
    ) {
        this.groupConversationId = groupConversationId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.senderAvatar = senderAvatar;
        this.groupConversationMessageId = groupConversationMessageId;
        this.content = content;
        this.sentAt = sentAt;
        this.updatedAt = updatedAt;
        this.type = type;
    }
}