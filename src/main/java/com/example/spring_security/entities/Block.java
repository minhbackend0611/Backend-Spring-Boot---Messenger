package com.example.spring_security.entities;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "Block",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"blocker_id", "blocked_user_id", "blocked_at"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Block {
    @EmbeddedId
    BlockId blockId;

    @ManyToOne
    @JoinColumn(name = "blocker_id", insertable = false, updatable = false)
    private User blocker;

    @ManyToOne
    @JoinColumn(name = "blocked_user_id", insertable = false, updatable = false)
    private User blockedUser;

    @Column(name = "blocked_at", insertable = false, updatable = false)
    private LocalDateTime blockedAt;

    @Column(name = "removed_at")
    private LocalDateTime removedAt;

    @Column(name = "is_active")
    private boolean isActive;
}
