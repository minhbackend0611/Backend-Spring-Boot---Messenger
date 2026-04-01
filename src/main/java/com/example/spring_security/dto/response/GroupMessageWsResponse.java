package com.example.spring_security.dto.response;

import com.example.spring_security.entities.Enum.MessageType;
import com.example.spring_security.entities.Enum.RealTimeAction;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class GroupMessageWsResponse {
    private Long groupConversationMessageId;

    private Long groupConversationId;

    private Long senderId;

    private String senderName;
    private String avatarUrl;

    private String content;

    private LocalDateTime sentAt;

    private LocalDateTime updatedAt;

    private MessageType type;

    private RealTimeAction realTimeAction;
}
