package com.example.spring_security.services.admin;

import com.example.spring_security.dto.request.ManageUserRequest;
import com.example.spring_security.dto.request.UpdateStatusReportRequest;
import com.example.spring_security.dto.response.*;
import com.example.spring_security.entities.GroupConversation;
import com.example.spring_security.entities.RecordSignIn;
import com.example.spring_security.entities.User;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ManagementUserService {
    List<User> getUserDetailList(String keyword, String username, String fullName, String email, Boolean isActive,
                                 Boolean isAccepted, Integer greaterThan,
                                 Integer smallerThan, String sort,
                                 Integer days);

    Map<String, String> createUser(ManageUserRequest createUserRequest);

    Map<String, String> deleteUser(Long userId);

    Map<String, String> updateUser(ManageUserRequest updateUserRequest, Long userId);

    ResetPasswordResponse resetPassword(Long userId);

    ListRecordSignInResponse getRecordSignIn(Boolean isSuccessful, Long userId, String username, LocalDate startDate, LocalDate endDate);

    ListUserFriendResponse getFriends(Long userId, String keyword, String sortBy);

    ListReportResponse getReports(String sortBy, String username, String email, LocalDate startDate, LocalDate endDate);

    Map<String, String> updateReports(UpdateStatusReportRequest updateStatusReportRequest);

    List<GroupConversationItemListResponse> getGroupList(String keyword, String sort);

    List<GroupMemberResponse> getMemberList(Long groupConversationId);

    List<GroupMemberResponse> getAdminList(Long groupConversationId);

    List<UserRecordOnlineResponse> getRecordOnline(String keyword, String sort, Long greaterThan, Long smallerThan);

    DashboardStatsResponse getDashboardStats(int year) throws JsonProcessingException;
}
