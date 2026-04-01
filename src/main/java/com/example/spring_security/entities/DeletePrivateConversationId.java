package com.example.spring_security.entities;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeletePrivateConversationId implements Serializable {

    @Column(name = "private_conversation_id")
    private Long privateConversationId;

    @Column(name = "user_id")
    private Long userId;
}
