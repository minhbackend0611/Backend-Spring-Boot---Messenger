package com.example.spring_security.websocket;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.spring_security.dto.response.NotificationWsResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WebSocketNotificationService {
    private final SimpMessagingTemplate messagingTemplate;

    public void sendToUser(String username, NotificationWsResponse payload) {
        messagingTemplate.convertAndSendToUser(username, "/client/notifications", payload);
    }
}
