package com.example.spring_security.websocket;

import com.example.spring_security.dto.response.PrivateMessageWsResponse;
import com.example.spring_security.dto.response.UserFriendResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebSocketStatusService {
    private final SimpMessagingTemplate messagingTemplate;


    public void sendStatusToFriend(String friendUsername, UserFriendResponse userFriendResponse) {
        messagingTemplate.convertAndSendToUser(
                friendUsername,
                "/client/friend-status",
                userFriendResponse
        );
    }
}