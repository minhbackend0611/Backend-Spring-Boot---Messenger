package com.example.spring_security.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlockId implements Serializable {
    @Column(name="blocker_id")
    private Long blockerId;
    @Column(name="blocked_user_id")
    private Long blockedUserId;
    @Column(name="blocked_at")
    LocalDateTime blockedAt;
}
