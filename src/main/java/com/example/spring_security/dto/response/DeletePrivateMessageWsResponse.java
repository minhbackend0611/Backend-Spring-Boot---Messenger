package com.example.spring_security.dto.response;

import com.example.spring_security.entities.Enum.RealTimeAction;
import lombok.Getter;
import lombok.experimental.SuperBuilder;


@Getter
@SuperBuilder
public class DeletePrivateMessageWsResponse extends BaseUserResponse {
    private Long privateConversationMessageId;

    private Long privateConversationId;

    private RealTimeAction realTimeAction;
}
