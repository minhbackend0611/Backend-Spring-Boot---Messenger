package com.example.spring_security.services.user;

import com.example.spring_security.dto.response.BaseUserResponse;
import com.example.spring_security.dto.response.MessageSearchResponse;
import com.example.spring_security.dto.response.UserSearchResponse;

import java.util.List;

public interface UserSearchService {
    List<UserSearchResponse> search(Long currentUserId, String keyword);

    List<MessageSearchResponse> searchPrivateMessage(Long userId, Long privateConversationId, String keyword);


    List<MessageSearchResponse> searchGroupMessages(Long userId, Long groupConversationId, String keyword);

    List<MessageSearchResponse> searchAllMessages(Long userId, String keyword);
}
