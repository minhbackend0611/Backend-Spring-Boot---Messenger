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
public class DeleteGroupConversationId implements Serializable {

    @Column(name = "group_conversation_id")
    private Long groupConversationId;

    @Column(name = "member_id")
    private Long memberId;

}
