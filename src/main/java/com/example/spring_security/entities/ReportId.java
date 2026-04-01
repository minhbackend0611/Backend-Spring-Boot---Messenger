package com.example.spring_security.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportId implements Serializable {

    @Column(name = "reporter_id")
    private Long reporterId;
    @Column(name = "reported_user_id")
    private Long reportedUserId;
    @Column(name = "reported_at")
    private LocalDateTime reportedAt;
}
