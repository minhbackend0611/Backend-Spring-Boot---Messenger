package com.example.spring_security.dto.response;


import com.example.spring_security.entities.Enum.MessageType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendMessageResponse {

    private Long messageId;

    private String content;

    private LocalDateTime sentAt;

    private LocalDateTime updatedAt;

    private MessageType type;

}

