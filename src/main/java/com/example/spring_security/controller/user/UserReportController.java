package com.example.spring_security.controller.user;

import com.example.spring_security.dto.request.ReportRequest;
import com.example.spring_security.entities.User;
import com.example.spring_security.services.user.UserReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat/user/report")
public class UserReportController {

    private final UserReportService userReportService;

    @PostMapping("/{id}")
    public ResponseEntity<Map<String, String>> report(@Valid @RequestBody ReportRequest reportRequest,
                                                      @AuthenticationPrincipal User user,
                                                      @PathVariable("id") Long id) {
        return ResponseEntity.ok(userReportService.report(reportRequest, user.getUserId(), id));
    }
}
