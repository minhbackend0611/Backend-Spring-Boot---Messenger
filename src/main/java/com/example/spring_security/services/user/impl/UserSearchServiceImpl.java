package com.example.spring_security.services.user.impl;


import com.example.spring_security.dto.response.BaseUserResponse;
import com.example.spring_security.dto.response.MessageSearchResponse;
import com.example.spring_security.dto.response.UserSearchResponse;
import com.example.spring_security.entities.User;
import com.example.spring_security.exception.CustomException;
import com.example.spring_security.repository.UserRepository;
import com.example.spring_security.services.user.UserSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserSearchServiceImpl implements UserSearchService {

    private final UserRepository userRepository;

    public List<UserSearchResponse> search(Long currentUserId, String keyword) {
        if (!userRepository.existsById(currentUserId))
            throw new CustomException(HttpStatus.NOT_FOUND, "User no longer exists.");
        List<UserSearchResponse> listUser = userRepository.searchUserByUsernameOrFullName(currentUserId, keyword);
        return listUser;
    }

    public List<MessageSearchResponse> searchPrivateMessage(Long userId, Long privateConversationId, String keyword) {
        return userRepository.searchPrivateMessages(privateConversationId, userId, keyword);
    }

    public List<MessageSearchResponse> searchGroupMessages(Long userId, Long groupConversationId, String keyword) {
        return userRepository.searchGroupMessages(groupConversationId, userId, keyword);
    }

    public List<MessageSearchResponse> searchAllMessages(Long userId, String keyword) {
        return userRepository.searchAllMessages(userId, keyword);
    }
}
