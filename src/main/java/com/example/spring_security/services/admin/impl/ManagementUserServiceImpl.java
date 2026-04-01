package com.example.spring_security.services.admin.impl;

import com.example.spring_security.dto.request.ManageUserRequest;
import com.example.spring_security.dto.request.UpdateStatusReportRequest;
import com.example.spring_security.dto.response.*;
import com.example.spring_security.entities.*;
import com.example.spring_security.entities.Enum.Gender;
import com.example.spring_security.entities.Enum.ReportStatus;
import com.example.spring_security.entities.Enum.Role;
import com.example.spring_security.entities.Token.RequestPasswordReset;
import com.example.spring_security.exception.CustomException;
import com.example.spring_security.repository.*;
import com.example.spring_security.repository.TokenRepo.RequestPasswordResetRepository;
import com.example.spring_security.services.admin.ManagementUserService;
import com.example.spring_security.services.third.EmailService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ManagementUserServiceImpl implements ManagementUserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final RequestPasswordResetRepository requestPasswordResetRepository;

    private final EmailService emailService;

    private final RecordSignInRepository recordSignInRepository;

    private final FriendRepository friendRepository;

    private final ReportRepository reportRepository;

    private final GroupConversationRepository groupConversationRepository;

    private final GroupConversationMemberRepository groupConversationMemberRepository;

    private final RecordOnlineUserRepository recordOnlineUserRepository;

    private final StatsRepository statsRepository;

    public List<User> getUserDetailList(String keyword, String username, String fullName, String email, Boolean isActive, Boolean isAccepted,
                                        Integer greaterThan, Integer smallerThan,
                                        String sort, Integer days) {
        System.out.println(username);
        List<User> listUserDetail = userRepository.managementUser(keyword, username, fullName, email, isActive, isAccepted, greaterThan, smallerThan, sort, days);
        return listUserDetail;
    }

    public Map<String, String> createUser(ManageUserRequest createUserRequest) {
        Map<String, String> msg = new HashMap<>();

        User user = new User();
        if (!userRepository.existsByUsername(createUserRequest.getUsername()))
            user.setUsername(createUserRequest.getUsername());
        else {
            throw new CustomException(HttpStatus.CONFLICT, "Username is already in use.");
        }
        if (!userRepository.existsByEmail(createUserRequest.getEmail())) {
            user.setEmail(createUserRequest.getEmail());
        }
        else {
            throw new CustomException(HttpStatus.CONFLICT, "Email is already in use.");
        }

        user.setHashPassword(passwordEncoder.encode(createUserRequest.getPassword()));
        if (createUserRequest.getGender() == null) user.setGender(Gender.HIDDEN);
        else user.setGender(createUserRequest.getGender());

        if (createUserRequest.getRole() == null) user.setRole(Role.USER);
        else user.setRole(createUserRequest.getRole());

        if (createUserRequest.getFirstName() == null) user.setFirstName("default first name");
        else user.setFirstName(createUserRequest.getFirstName());

        if (createUserRequest.getLastName() == null) user.setLastName("default last name");
        else user.setLastName(createUserRequest.getLastName());

        user.setBirthday(createUserRequest.getBirthDay());
        user.setAddress(createUserRequest.getAddress());
        user.setAvatarUrl(createUserRequest.getAvatarUrl());

        if (createUserRequest.getIsOnline() != null)
            user.setIsOnline(createUserRequest.getIsOnline());
        else user.setIsOnline(false);

        if (createUserRequest.getIsActive() != null)
            user.setIsActive(createUserRequest.getIsActive());
        else user.setIsActive(true);

        if (createUserRequest.getIsAccepted() != null)
            user.setIsAccepted(createUserRequest.getIsAccepted());
        else user.setIsAccepted(true);

        if (createUserRequest.getJoinedAt() != null)
            user.setJoinedAt(createUserRequest.getJoinedAt());
        else user.setJoinedAt(LocalDateTime.now());

        if (createUserRequest.getUpdatedAt() != null)
            user.setUpdatedAt(createUserRequest.getUpdatedAt());

        userRepository.save(user);

        msg.put("message", "Created user successfully.");
        return msg;
    }


    public Map<String, String> deleteUser(Long userId) {
        if (!userRepository.existsById(userId))
            throw new CustomException(HttpStatus.NOT_FOUND, "User no longer exists");
        Map<String, String> msg = new HashMap<>();
        userRepository.deleteById(userId);
        msg.put("message", "Deleted successfully");
        return msg;
    }

    public Map<String, String> updateUser(ManageUserRequest updateUserRequest, Long userId) {
        if (!userRepository.existsById(userId))
            throw new CustomException(HttpStatus.NOT_FOUND, "User no longer exists");
        Map<String, String> msg = new HashMap<>();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND,"User no longer exists"));

        String username = updateUserRequest.getUsername();

        String email = updateUserRequest.getEmail();

        String password = updateUserRequest.getPassword();

        Role role = updateUserRequest.getRole();

        Gender gender = updateUserRequest.getGender();

        String firstName = updateUserRequest.getFirstName();

        String lastName = updateUserRequest.getLastName();

        LocalDate birthDay = updateUserRequest.getBirthDay();

        String address = updateUserRequest.getAddress();

        String avatarUrl = updateUserRequest.getAvatarUrl();

        Boolean isActive = updateUserRequest.getIsActive();

        Boolean isOnline = updateUserRequest.getIsOnline();

        Boolean isAccepted = updateUserRequest.getIsAccepted();

        LocalDateTime joinedAt = updateUserRequest.getJoinedAt();

        LocalDateTime updatedAt = updateUserRequest.getUpdatedAt();

        if (username != null)
            if (!userRepository.existsByUsername(username)) user.setUsername(username);
            else throw new CustomException(HttpStatus.CONFLICT, "Username is already in use.");

        if (email != null)
            if (!userRepository.existsByEmail(email)) user.setEmail(email);
            else throw new CustomException(HttpStatus.CONFLICT, "Email is already in use.");

        if (password != null) user.setHashPassword(passwordEncoder.encode(password));

        if (role != null) user.setRole(role);

        if (birthDay != null) user.setBirthday(birthDay);

        if (gender != null) user.setGender(gender);

        if (firstName != null) user.setFirstName(firstName);

        if (lastName != null) user.setLastName(lastName);

        if (address != null) user.setAddress(address);

        if (avatarUrl != null) user.setAvatarUrl(avatarUrl);

        if (isActive != null) user.setIsActive(isActive);

        if (isOnline != null) user.setIsOnline(isOnline);

        if (isAccepted != null) user.setIsAccepted(isAccepted);

        if (joinedAt != null) user.setJoinedAt(joinedAt);

        if (updatedAt != null) user.setUpdatedAt(updatedAt);

        userRepository.save(user);

        msg.put("message", "Update successfully");
        return msg;
    }

    public ResetPasswordResponse resetPassword(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "User no longer exists."));

        RequestPasswordReset requestpasswordreset = requestPasswordResetRepository.findByUser(user).orElse(new RequestPasswordReset());

        String generatedPassword = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        user.setHashPassword(passwordEncoder.encode(generatedPassword));

        requestpasswordreset.setCreatedAt(LocalDateTime.now());
        requestpasswordreset.setToken(generatedPassword);
        requestpasswordreset.setUser(user);
        userRepository.save(user);
        requestPasswordResetRepository.save(requestpasswordreset);

        emailService.sendEmail(
                user.getEmail(),
                "Your New Password",
                "Hi " + user.getUsername() + ",\n\n" +
                        "We’ve reset your password as requested.\n\n" +
                        "Your new temporary password is: " + generatedPassword + "\n\n" +
                        "Please log in using this password and change it immediately for security reasons.\n\n" +
                        "Best regards,\n" +
                        "Your Support Team"
        );

        ResetPasswordResponse resetPasswordResponse = new ResetPasswordResponse();
        resetPasswordResponse.setPassword(generatedPassword);
        return resetPasswordResponse;
    };

    public ListRecordSignInResponse getRecordSignIn(Boolean isSuccessful, Long userId, String username, LocalDate startDate, LocalDate endDate) {

        ListRecordSignInResponse listRecordSignInResponse = new ListRecordSignInResponse();

        List<RecordSignInResponse> recordSignInResponseList = recordSignInRepository.findAll(isSuccessful, userId, username);

        List<RecordSignInResponse> filtered = recordSignInResponseList.stream().filter(
                r -> (startDate == null || r.getSignedInAt().isAfter(startDate.atStartOfDay()))
                        && (endDate == null || r.getSignedInAt().isBefore(endDate.atTime(LocalTime.MAX)))
        ).collect(Collectors.toList());

        Long countTotal = filtered.stream().count();

        Long countSuccess = filtered.stream().filter(f -> f.getIsSuccessful()).count();

        Long countFailed = filtered.stream().filter(f -> !f.getIsSuccessful()).count();

        listRecordSignInResponse.setRecordSignInResponseList(filtered);
        listRecordSignInResponse.setTotal(countTotal);
        listRecordSignInResponse.setCountFailed(countFailed);
        listRecordSignInResponse.setCountSuccess(countSuccess);
        return listRecordSignInResponse;
    }

    public ListUserFriendResponse getFriends(Long userId, String keyword, String sortBy) {

        if (keyword == null) keyword = "";

        if (sortBy == null) sortBy = "isOnline";

        List<UserFriendResponse> userFriendResponseList = friendRepository
                .findAllFriendsByUserIdAndKeywordOrderBy(userId, keyword, sortBy);



        ListUserFriendResponse listUserFriendResponse = new ListUserFriendResponse
                (userFriendResponseList, friendRepository.countFriends(userId, keyword));

        return listUserFriendResponse;
    }

    public ListReportResponse getReports(String sortBy, String username, String email, LocalDate startDate, LocalDate endDate) {

        List<Report>reportList = reportRepository.findReportsWithFilterAndOrderBy(sortBy, username, email);

        List<Report> reportListFilter = reportList.stream().filter(
          r -> (startDate == null || r.getId().getReportedAt().isAfter(startDate.atStartOfDay()))
                  && (endDate == null || r.getId().getReportedAt().isBefore(endDate.atTime(LocalTime.MAX)))
        ).collect(Collectors.toList());

        List<ReportResponse>reportResponseList = reportListFilter.stream().map(r -> ReportResponse.builder()
                .reporterId(r.getId().getReporterId())
                .reportedUserId(r.getId().getReportedUserId())
                .reporterUsername(r.getReporter().getUsername())
                .reportedUserUsername(r.getReportedUser().getUsername())
                .reporterFullName(r.getReporter().getFirstName() + " " + r.getReporter().getLastName())
                .reportedUserFullName(r.getReportedUser().getFirstName() + " " + r.getReportedUser().getLastName())
                .reporterAvtUrl(r.getReporter().getAvatarUrl()) .reportedUserAvtUrl(r.getReportedUser().getAvatarUrl())
                .reason(r.getReason())
                .status(r.getStatus())
                .reportedAt(r.getId().getReportedAt())
                .build()).collect(Collectors.toList());
        ListReportResponse listReportResponse = ListReportResponse.builder()
                .reportResponseList(reportResponseList)
                .count(reportResponseList.size())
                .build();

        return listReportResponse;
    }

    public List<GroupConversationItemListResponse> getGroupList(String keyword, String sort) {

        return groupConversationRepository.managementList(keyword, sort);
    }

    public List<GroupMemberResponse> getMemberList(Long groupConversationId) {
        return groupConversationMemberRepository.findMembersByGroupConversationId(groupConversationId);
    }

    public List<GroupMemberResponse> getAdminList(Long groupConversationId) {
        return groupConversationMemberRepository.findAdminsByGroupConversationId(groupConversationId);
    }

    public List<UserRecordOnlineResponse> getRecordOnline(String keyword, String sort, Long greaterThan, Long smallerThan) {
        return recordOnlineUserRepository.managementRecordList(keyword, sort, greaterThan, smallerThan);
    }

    public Map<String, String> updateReports(UpdateStatusReportRequest updateStatusReportRequest) {
        ReportId reportId = ReportId.builder()
                .reporterId(updateStatusReportRequest.getReporterId())
                .reportedUserId(updateStatusReportRequest.getReportedUserId())
                .reportedAt(updateStatusReportRequest.getReportedAt())
                .build();
        Report report = reportRepository.findById(reportId).orElseThrow(
                () -> new CustomException(HttpStatus.NOT_FOUND, "This report not found.")
        );

        report.setStatus(updateStatusReportRequest.getStatus());

        System.out.println("Before");

        reportRepository.save(report);

        System.out.println("After");

        if (updateStatusReportRequest.getStatus() == ReportStatus.LOCKED) {
            User user = userRepository.findById(reportId.getReportedUserId()).orElseThrow(
                    () -> new CustomException(HttpStatus.NOT_FOUND, "User not found.")
            );
            user.setIsActive(false);
            userRepository.save(user);
        }

        Map<String, String> msg = new HashMap<>();

        msg.put("message", "Updated successfully.");

        return msg;

    }



    public DashboardStatsResponse getDashboardStats(int year) throws JsonProcessingException {
        String json = statsRepository.getDashboardStats(year);

        ObjectMapper mapper = new ObjectMapper();
        DashboardStatsResponse dto =
                mapper.readValue(json, DashboardStatsResponse.class);

        return dto;
    }
}
