package com.example.spring_security.controller.admin;

import com.example.spring_security.dto.request.ManageUserRequest;
import com.example.spring_security.dto.request.UpdateStatusReportRequest;
import com.example.spring_security.dto.response.*;
import com.example.spring_security.entities.*;
import com.example.spring_security.services.admin.ManagementUserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat/admin")
@RequiredArgsConstructor
public class UserManagementController {

    private final ManagementUserService managementUserService;

    @GetMapping("")
    public ResponseEntity<String> sayHello() {
        return ResponseEntity.ok("Hi admin!");
    }

    @GetMapping("/get-user")
    public ResponseEntity<List<User>> getUserDetailList
            (@RequestParam(value = "keyword", required = false) String keyword,
             @RequestParam(value = "username", required = false) String username,
             @RequestParam(value = "fullName", required = false) String fullName,
             @RequestParam(value = "email", required = false) String email,
             @RequestParam(value = "isActive", required = false) Boolean isActive,
             @RequestParam(value = "isAccepted", required = false) Boolean isAccepted,
             @RequestParam(value = "greaterThan", required = false) Integer greaterThan,
             @RequestParam(value = "smallerThan", required = false) Integer smallerThan,
             @RequestParam(value = "sort", required = false) String sort,
             @RequestParam(value = "days", required = false) Integer days) {
        return ResponseEntity.ok(managementUserService.getUserDetailList(keyword, username, fullName, email, isActive, isAccepted, greaterThan, smallerThan, sort, days));
    }

    @PostMapping("/create-user")
    public ResponseEntity<Map<String, String>> createUser(@RequestBody @Valid ManageUserRequest createUserRequest) {
        return ResponseEntity.ok(managementUserService.createUser(createUserRequest));
    }

    @PatchMapping("/update-user/{userId}")
    public ResponseEntity<Map<String, String>> updateUser(@RequestBody ManageUserRequest updateUserRequest, @PathVariable("userId") Long userId) {
        return ResponseEntity.ok(managementUserService.updateUser(updateUserRequest, userId));
    }

    @DeleteMapping("/delete-user/{userId}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(managementUserService.deleteUser(userId));
    }

    @PostMapping("/reset-password/{userId}")
    public ResponseEntity<ResetPasswordResponse> resetPassword(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(managementUserService.resetPassword(userId));
    }

    @GetMapping("/get-record-signin")
    public ResponseEntity<ListRecordSignInResponse> getRecordSignIn
            (@RequestParam(value = "isSuccessful", required = false) Boolean isSuccessful,
            @RequestParam(value = "userId", required = false) Long userId,
             @RequestParam(value = "username", required = false) String username,
             @RequestParam(value = "startDate", required = false)  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
             @RequestParam(value = "endDate", required = false)  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(managementUserService.getRecordSignIn(isSuccessful, userId, username, startDate, endDate));
    }

    @GetMapping("/get-friend/{userId}")
    public ResponseEntity<ListUserFriendResponse> getFriends
            (@PathVariable(value = "userId", required = true) Long userId,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "sortBy", required = false) String sortBy) {
        return ResponseEntity.ok(managementUserService.getFriends(userId, keyword, sortBy));
    }

    @GetMapping("/get-report")
    public ResponseEntity<ListReportResponse> getReport
            (@RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "startDate", required = false)  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
             @RequestParam(value = "endDate", required = false)  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(managementUserService.getReports(sortBy, username, email, startDate, endDate));
    }

    @PatchMapping("/update-report")
    public ResponseEntity<Map<String, String>> updateReport
            (@RequestBody UpdateStatusReportRequest updateStatusReportRequest) {
        return ResponseEntity.ok(managementUserService.updateReports(updateStatusReportRequest));
    }

    @GetMapping("/get-group")
    public ResponseEntity<List<GroupConversationItemListResponse>> getGroup
            (@RequestParam(value = "keyword", required = false) String keyword,
             @RequestParam(value = "sort", required = false) String sort) {
        return ResponseEntity.ok(managementUserService.getGroupList(keyword, sort));
    }

    @GetMapping("/get-group/{groupConversationId}/member")
    public ResponseEntity<List<GroupMemberResponse>> getMember(@PathVariable(value = "groupConversationId", required = true) Long groupConversationId) {
        return ResponseEntity.ok(managementUserService.getMemberList(groupConversationId));
    }

    @GetMapping("/get-group/{groupConversationId}/admin")
    public ResponseEntity<List<GroupMemberResponse>> getAdmin(@PathVariable(value = "groupConversationId", required = true) Long groupConversationId) {
        return ResponseEntity.ok(managementUserService.getAdminList(groupConversationId));
    }

    @GetMapping("/get-record-online")
    public ResponseEntity<List<UserRecordOnlineResponse>> getRecordOnline
            (@RequestParam(value = "keyword", required = false) String keyword,
             @RequestParam(value = "sort", required = false) String sort,
             @RequestParam(value = "greaterThan", required = false) Long greaterThan,
             @RequestParam(value = "smallerThan", required = false) Long smallerThan) {
        return ResponseEntity.ok(managementUserService.getRecordOnline(keyword, sort, greaterThan, smallerThan));
    }

    @GetMapping("/analytics")
    public ResponseEntity<DashboardStatsResponse> getDashboardStats
            (@RequestParam(value = "year") int year) throws JsonProcessingException {
        return ResponseEntity.ok(managementUserService.getDashboardStats(year));
    }

}
