package com.example.spring_security.dto.response;

import com.example.spring_security.entities.Enum.MessageType;
import com.example.spring_security.entities.Enum.RealTimeAction;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Setter
@SuperBuilder
@Getter
public class PrivateMessageWsResponse extends BaseUserResponse {
    private Long privateConversationId;

    private Long privateConversationMessageId;

    private String content;

    private LocalDateTime sentAt;

    private LocalDateTime updatedAt;

    private MessageType type;

    private RealTimeAction realTimeAction;
}
