package com.example.spring_security.services.third.impl;

import com.example.spring_security.services.third.WebSocketSenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;


@Service
public class WebSocketSenderServiceImpl implements WebSocketSenderService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public WebSocketSenderServiceImpl(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

}
