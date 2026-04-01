package com.example.spring_security.dto.response;

import com.example.spring_security.entities.Enum.RealTimeAction;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class DeleteGroupMessageWsResponse extends BaseUserResponse {

    private Long groupConversationMessageId;

    private Long groupConversationId;

    private Boolean isAll;

    private RealTimeAction realTimeAction;

}
