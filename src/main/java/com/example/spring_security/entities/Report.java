package com.example.spring_security.entities;

import com.example.spring_security.entities.Enum.ReportStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "report")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {
    @EmbeddedId
    private ReportId id;

    @ManyToOne
    @JoinColumn(name = "reporter_id", insertable = false, updatable = false)
    private User reporter;

    @ManyToOne
    @JoinColumn(name = "reported_user_id", insertable = false, updatable = false)
    private User reportedUser;

    @Column(name = "reason")
    private String reason;

    @Column(name = "status")
    private ReportStatus status;

}
