package com.example.spring_security.services.user.impl;

import com.example.spring_security.dto.response.ConversationListItem;
import com.example.spring_security.repository.PrivateConversationRepository;
import com.example.spring_security.services.user.UserConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserConversationServiceImpl implements UserConversationService {

    private final PrivateConversationRepository privateConversationRepository;

    public List<ConversationListItem> getConversationList(Long userId) {
        return privateConversationRepository.getConversationList(userId);
    }

}
