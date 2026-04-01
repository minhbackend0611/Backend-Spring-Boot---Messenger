package com.example.spring_security.services.user;

import com.example.spring_security.dto.response.ConversationListItem;

import java.util.List;

public interface UserConversationService {
    List<ConversationListItem> getConversationList(Long userId);
}


