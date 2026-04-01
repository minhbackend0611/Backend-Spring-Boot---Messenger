package com.example.spring_security.websocket;

import com.example.spring_security.dto.response.DeletePrivateMessageWsResponse;
import com.example.spring_security.dto.response.PrivateMessageWsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebSocketPrivateMessageService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendMessageToUser(String receiver, PrivateMessageWsResponse message) {
        messagingTemplate.convertAndSendToUser(
                receiver,
                "/client/messages",
                message
        );
    }

    public void sendDeleteMessage(String receiverUsername, DeletePrivateMessageWsResponse payload) {
        messagingTemplate.convertAndSendToUser(
                receiverUsername,
                "/client/messages",
                payload
        );
    }
}
