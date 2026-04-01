package com.example.spring_security.dto.response;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ListPrivateConversationMessageResponse extends BaseUserResponse {

    private Boolean isOnline;

    private Long privateConversationId;

    private List<PrivateConversationMessageResponse> privateConversationMessageResponseList;
}
