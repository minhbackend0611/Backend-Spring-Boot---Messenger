package com.example.spring_security.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class ListGroupConversationItemResponse {
    private Long memberCount;
    private Long adminCount;
    private String usernameOwner;
    List<GroupConversationItemListResponse> groupConversationItemListResponses;
}
