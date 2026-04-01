package com.example.spring_security.dto.response;

import com.example.spring_security.entities.Enum.ConversationType;
import com.example.spring_security.entities.Enum.MessageType;
import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
public class MessageSearchResponse {

    private Long id;

    private Long messageId;

    private String avatarUrl;

    private String name;

    private String content;

    private LocalDateTime sentAt;

    private LocalDateTime updatedAt;

    private MessageType messageType;

    private ConversationType conversationType;

    private String conversationName;

    public MessageSearchResponse(Long id, Long messageId, String avatarUrl, String name,
                                 String content, java.sql.Timestamp sentAt,
                                 java.sql.Timestamp updatedAt, Short messageType,
                                 Short conversationType, String conversationName) {
        this.id = id;
        this.conversationName = conversationName;
        this.messageId = messageId;
        this.avatarUrl = avatarUrl;
        this.name = name;
        this.content = content;
        this.sentAt = sentAt != null ? sentAt.toLocalDateTime() : null;
        this.updatedAt = updatedAt != null ? updatedAt.toLocalDateTime() : null;

        MessageType.Converter msgConverter = new MessageType.Converter();

        this.messageType = msgConverter.fromShort(messageType);

        ConversationType.Converter conversationConverter = new ConversationType.Converter();

        this.conversationType = conversationConverter.fromShort(conversationType);
    }

}
