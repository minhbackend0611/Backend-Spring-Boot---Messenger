package com.example.spring_security.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class DeleteGroupConversationMessageId implements Serializable {
    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "group_conversation_message_id")
    private Long groupConversationMessageId;
}
