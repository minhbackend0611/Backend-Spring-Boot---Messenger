package com.example.spring_security.controller.user;

import com.example.spring_security.dto.request.ChangeEmailRequest;
import com.example.spring_security.dto.request.ChangePasswordRequest;
import com.example.spring_security.dto.request.UpdateProfileRequest;
import com.example.spring_security.dto.response.UserProfileResponse;
import com.example.spring_security.entities.User;
import com.example.spring_security.services.user.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/chat/user/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping("")
    public ResponseEntity<UserProfileResponse> getProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userProfileService.getProfile(user));
    }

    @PatchMapping("/update-profile")
    public ResponseEntity<?> updateProfile(
            @Valid @RequestBody UpdateProfileRequest updateProfileRequest,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userProfileService.updateProfile(updateProfileRequest, user));
    }

    @PutMapping("/update-avatar")
    public ResponseEntity<?> updateAvatar(@AuthenticationPrincipal User user,
            @RequestParam("avatar") MultipartFile avatar) {
        return ResponseEntity.ok(Map.of("avatarUrl", userProfileService.updateAvatar(user, avatar)));
    }

    @DeleteMapping("/remove-avatar")
    public ResponseEntity<?> removeAvatar(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userProfileService.removeAvatar(user));
    }

    @PostMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(
            @Valid @RequestBody ChangePasswordRequest changePasswordRequest,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userProfileService.changePassword(changePasswordRequest, user));
    }

    @GetMapping("/create-token")
    public ResponseEntity<Map<String, String>> createToken(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userProfileService.createToken(user));
    }

    @PostMapping("/change-email")
    public ResponseEntity<Map<String, String>> changeEmail(@RequestParam("token") String token,
            @Valid @RequestBody ChangeEmailRequest changeEmailRequest,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userProfileService.changeEmail(token, changeEmailRequest, user));
    }

    @GetMapping("/apply-change-email")
    public ResponseEntity<Map<String, String>> applyChangeEmail(@RequestParam("token") String token) {
        return ResponseEntity.ok(userProfileService.applyChangeEmail(token));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponse> getUserProfile(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userProfileService.getUserProfile(id));
    }
}
