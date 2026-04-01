package com.example.spring_security.services.user.impl;

import com.example.spring_security.dto.request.ReportRequest;
import com.example.spring_security.entities.Enum.ReportStatus;
import com.example.spring_security.entities.Report;
import com.example.spring_security.entities.ReportId;
import com.example.spring_security.exception.CustomException;
import com.example.spring_security.repository.ReportRepository;
import com.example.spring_security.repository.UserRepository;
import com.example.spring_security.services.user.UserReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserReportServiceImpl implements UserReportService {

    private final UserRepository userRepository;

    private final ReportRepository reportRepository;

    public Map<String, String> report(ReportRequest reportRequest, Long reporterId, Long reportedUserId) {

        if (reportedUserId.equals(reporterId)) {
            throw new CustomException(HttpStatus.CONFLICT, "Illegal behavior. You can not report yourself.");
        }

        ReportId reportId = new ReportId(reporterId, reportedUserId, LocalDateTime.now());
        Report report = Report.builder()
                .id(reportId)
                .reporter(userRepository.findById(reporterId)
                        .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "User no longer exists.")))
                .reportedUser(userRepository.findById(reportedUserId)
                        .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "User no longer exists.")))
                .reason(reportRequest.getReason())
                .status(ReportStatus.PENDING)
                .build();

        Map<String, String> message = new HashMap<>();

        message.put("message", "Reported successfully.");

        reportRepository.save(report);

        return message;
    }
}
