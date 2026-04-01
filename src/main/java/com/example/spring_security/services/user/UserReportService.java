package com.example.spring_security.services.user;

import com.example.spring_security.dto.request.ReportRequest;

import java.util.Map;

public interface UserReportService {
    Map<String, String> report(ReportRequest reportRequest, Long reporterId, Long reportedUserId);
}
