package com.example.spring_security.dto.request;

import com.example.spring_security.entities.Enum.ReportStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UpdateStatusReportRequest {
    private Long reporterId;
    private Long reportedUserId;
    private LocalDateTime reportedAt;
    private ReportStatus status;
}
