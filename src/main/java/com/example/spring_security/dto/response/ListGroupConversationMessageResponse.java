package com.example.spring_security.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListGroupConversationMessageResponse {

    private Long groupConversationId;

    private String avatarUrl;

    private String groupName;

    private Boolean isEncrypted;

    private List<GroupMemberResponse> groupMemberResponseList;

    private List<GroupConversationMessageResponse> groupConversationMessageResponseList;


}
