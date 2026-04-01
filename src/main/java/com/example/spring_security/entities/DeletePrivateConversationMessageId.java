package com.example.spring_security.entities;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class DeletePrivateConversationMessageId implements Serializable {
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "private_conversation_message_id")
    private Long privateConversationMessageId;
}
