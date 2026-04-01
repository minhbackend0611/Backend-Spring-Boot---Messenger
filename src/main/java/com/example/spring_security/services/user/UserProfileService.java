package com.example.spring_security.services.user;

import com.example.spring_security.dto.request.ChangePasswordRequest;
import com.example.spring_security.dto.request.ChangeEmailRequest;
import com.example.spring_security.dto.request.UpdateProfileRequest;
import com.example.spring_security.dto.response.UserProfileResponse;
import com.example.spring_security.entities.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface UserProfileService {
    UserProfileResponse getProfile(User user);
    String updateAvatar(User user, MultipartFile avatar);
    Map<String, String> removeAvatar(User user);
    Map<String, Object> updateProfile(UpdateProfileRequest updateProfileRequest, User user);
    Map<String, String> changePassword(ChangePasswordRequest changPasswordRequest, User user);
    Map<String, String> changeEmail(String token, ChangeEmailRequest changeEmailRequest, User user);
    Map<String, String> createToken(User user);
    Map<String, String> applyChangeEmail(String token);
    UserProfileResponse getUserProfile(Long id);
}
