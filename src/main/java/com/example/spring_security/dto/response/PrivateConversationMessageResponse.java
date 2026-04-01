package com.example.spring_security.dto.response;

import com.example.spring_security.entities.Enum.MessageType;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
public class PrivateConversationMessageResponse {

    private Long privateConversationId;
    private Long senderId;
    private String senderName;
    private String senderAvatar;
    private Long privateConversationMessageId;
    private String content;
    private LocalDateTime sentAt;
    private LocalDateTime updatedAt;
    private MessageType type;

    public PrivateConversationMessageResponse(
            Long privateConversationId,
            Long senderId,
            String senderName,
            String senderAvatar,
            Long privateConversationMessageId,
            String content,
            LocalDateTime sentAt, // Matches pcm.sentAt
            LocalDateTime updatedAt, // Matches pcm.updatedAt
            MessageType type // Matches pcm.type
    ) {
        this.privateConversationId = privateConversationId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.senderAvatar = senderAvatar;
        this.privateConversationMessageId = privateConversationMessageId;
        this.content = content;
        this.sentAt = sentAt;
        this.updatedAt = updatedAt;
        this.type = type;
    }
}