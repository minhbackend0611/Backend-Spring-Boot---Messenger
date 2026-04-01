package com.example.spring_security.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "record_online_user")
@NoArgsConstructor
public class RecordOnlineUser {

    @Id
    @Column(name = "session_id")
    private String sessionId;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "online_at")
    LocalDateTime onlineAt;
    @Column(name = "offline_at")
    LocalDateTime offlineAt;

}
