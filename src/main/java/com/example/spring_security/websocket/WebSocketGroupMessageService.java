package com.example.spring_security.websocket;

import com.example.spring_security.dto.response.DeleteGroupMessageWsResponse;
import com.example.spring_security.dto.response.GroupMessageWsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebSocketGroupMessageService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendGroupMessage(Long groupId, GroupMessageWsResponse groupMessageWsResponse) {
        messagingTemplate.convertAndSend(
                "/client/group/" + groupId, // topic broadcast
                groupMessageWsResponse
        );
    }

    public void sendDeleteGroupMessage(Long groupId, DeleteGroupMessageWsResponse deleteGroupMessageWsResponse) {
        messagingTemplate.convertAndSend(
                "/client/group/" + groupId, // topic broadcast
                deleteGroupMessageWsResponse
        );
    }
}
