package com.example.spring_security.services.user.impl;

import com.example.spring_security.dto.request.ChangePasswordRequest;
import com.example.spring_security.dto.request.ChangeEmailRequest;
import com.example.spring_security.dto.request.UpdateProfileRequest;
import com.example.spring_security.dto.response.UserProfileResponse;
import com.example.spring_security.entities.Token.VerifyEmailChangeToken;
import com.example.spring_security.entities.Token.VerifyToken;
import com.example.spring_security.entities.User;
import com.example.spring_security.exception.CustomException;
import com.example.spring_security.repository.UserRepository;
import com.example.spring_security.repository.TokenRepo.VerifyEmailChangeTokenRepository;
import com.example.spring_security.repository.TokenRepo.VerifyTokenRepository;
import com.example.spring_security.services.third.CloudService;
import com.example.spring_security.services.third.EmailService;
import com.example.spring_security.services.third.JWTService;
import com.example.spring_security.services.user.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final VerifyTokenRepository verifyTokenRepository;

    private final EmailService emailService;

    private final VerifyEmailChangeTokenRepository verifyEmailChangeTokenRepository;

    private final CloudService cloudService;

    private final JWTService jwtService;

    public String updateAvatar(User user, MultipartFile avatar) {
        if (avatar.isEmpty())
            throw new CustomException(HttpStatus.BAD_REQUEST, "There is no avatar to set.");
        else {
            try {
                String avatarUrl = cloudService.uploadAvatar(avatar, user.getUsername());
                user.setAvatarUrl(avatarUrl);
                userRepository.save(user);
                return avatarUrl;
            } catch (IOException err) {
                throw new CustomException(HttpStatus.BAD_REQUEST, "Something went wrong. Unable to set avatar.");
            } catch (Exception e) {
                throw e;
            }
        }
    }

    public Map<String, String> removeAvatar(User user) {
        user.setAvatarUrl(null);
        userRepository.save(user);
        Map<String, String> msg = new HashMap<>();

        msg.put("message", "Remove successfully.");

        return msg;
    }

    public UserProfileResponse getProfile(User user) {
        return new UserProfileResponse(user);
    }

    public Map<String, Object> updateProfile(UpdateProfileRequest updateProfileRequest, User user) {
        String token = "";
        String refreshToken = "";
        if (updateProfileRequest.getUsername() != null) {
            if (userRepository.existsByUsername(updateProfileRequest.getUsername())) {
                throw new CustomException(HttpStatus.CONFLICT, "This username is already in use.");
            }
            user.setUsername(updateProfileRequest.getUsername());
            userRepository.save(user);
            token = jwtService.generateToken(user);
            refreshToken = jwtService.generateRefreshToken(new HashMap<>(), user);
        }

        if (updateProfileRequest.getEmail() != null) {
            if (userRepository.existsByEmail(updateProfileRequest.getEmail())) {
                throw new CustomException(HttpStatus.CONFLICT, "This email is already in use.");
            }
            user.setEmail(updateProfileRequest.getEmail());
        }

        if (updateProfileRequest.getFirstName() != null) {
            user.setFirstName(updateProfileRequest.getFirstName());
        }

        if (updateProfileRequest.getLastName() != null) {
            user.setLastName(updateProfileRequest.getFirstName());
        }

        if (updateProfileRequest.getAddress() != null) {
            user.setAddress(updateProfileRequest.getAddress());
        }

        if (updateProfileRequest.getGender() != null) {
            user.setGender(updateProfileRequest.getGender());
        }

        if (updateProfileRequest.getBirthday() != null) {
            user.setBirthday(updateProfileRequest.getBirthday());
        }

        userRepository.save(user);

        UserProfileResponse userProfileResponse = new UserProfileResponse(user);

       Map<String, Object> response = new HashMap<>();

        response.put("userProfile", userProfileResponse);
        response.put("token", token);
        response.put("refreshToken", refreshToken);

        return response;
    }

    public Map<String, String> changePassword(ChangePasswordRequest changePasswordRequest, User user) {
        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Password is incorrect.");
        }

        if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match.");
        }

        if (changePasswordRequest.getNewPassword().equals(changePasswordRequest.getOldPassword())) {
            throw new RuntimeException("The new password cannot be the same as your current password.");
        }
        user.setHashPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(user);
        Map<String, String> msg = new HashMap<>();
        msg.put("message", "Password changed successfully. Please use your new password for future logins.");
        return msg;
    }

    public Map<String, String> changeEmail(String token, ChangeEmailRequest changeEmailRequest, User user) {

        VerifyToken verifyToken = verifyTokenRepository.findByToken(token).orElse(null);

        if (verifyToken == null || !verifyToken.getUser().getUsername().equals(user.getUsername())) {
            throw new RuntimeException(
                    "Your email verification request could not be completed. Please request a new verification token.");
        }

        if (verifyToken.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException(
                    "This email verification token is no longer valid. Please request a new verification link.");
        }

        if (changeEmailRequest.getEmail().equals(user.getEmail())) {
            throw new RuntimeException("The new email address must be different from the current one.");
        }
        if (userRepository.existsByEmail(changeEmailRequest.getEmail())) {
            throw new RuntimeException("This email address is already in use by another account.");
        }

        String randomToken = UUID.randomUUID().toString().replace("-", "").substring(0, 16);

        VerifyEmailChangeToken verifyEmailChangeToken = verifyEmailChangeTokenRepository.findByUser(user)
                .orElse(new VerifyEmailChangeToken());

        verifyEmailChangeToken.setNewEmail(changeEmailRequest.getEmail());
        verifyEmailChangeToken.setUser(user);
        verifyEmailChangeToken.setCreatedAt(LocalDateTime.now());
        verifyEmailChangeToken.setExpiredAt(LocalDateTime.now().plusMinutes(10));
        verifyEmailChangeToken.setToken(randomToken);
        verifyEmailChangeTokenRepository.save(verifyEmailChangeToken);

        LocalDateTime expiryDate = verifyEmailChangeToken.getExpiredAt();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm, MMM dd, yyyy");
        String formattedExpiry = expiryDate.format(formatter);

        emailService.sendEmail(
                user.getEmail(),
                "Verify your new email",
                "Hi " + user.getUsername() + ",\n\n" +
                        "Please use the verification token below to verify your new email:\n" +
                        randomToken + "\n\n" +
                        "This token will expire in 10 minutes.");
        Map<String, String> msg = new HashMap<>();
        msg.put("message", "Please check your new email to verify the change.");
        return msg;
    }

    public Map<String, String> createToken(User user) {
        VerifyToken verifyToken = verifyTokenRepository.findByUser(user).orElse(new VerifyToken());

        if (verifyToken.getId() != null && verifyToken.getCreatedAt().plusMinutes(10).isAfter(LocalDateTime.now())) {
            throw new RuntimeException("Please wait before sending another change email request.");
        }

        String randomToken = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        verifyToken.setUser(user);
        verifyToken.setToken(randomToken);
        verifyToken.setCreatedAt(LocalDateTime.now());
        verifyToken.setExpiredAt(LocalDateTime.now().plusMinutes(30));
        verifyTokenRepository.save(verifyToken);
        emailService.sendEmail(
                user.getEmail(),
                "Proceed with Changing Your Email",
                "Hi " + user.getUsername() + ",\n\n" +
                        "To proceed with updating your email address, please use the verification token below:\n\n" +
                        randomToken + "\n\n" +
                        "This token will expire in 30 minutes.");
        Map<String, String> msg = new HashMap<>();
        msg.put("message",
                "Please check your email to get the verification token to proceed with changing your email.");
        return msg;
    }

    public Map<String, String> applyChangeEmail(String token) {
        VerifyEmailChangeToken verifyEmailChangeToken = verifyEmailChangeTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token."));
        if (verifyEmailChangeToken.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token is already expired");
        }
        User user = verifyEmailChangeToken.getUser();
        user.setEmail(verifyEmailChangeToken.getNewEmail());
        userRepository.save(user);
        Map<String, String> msg = new HashMap<>();
        msg.put("message", "Your email has been successfully updated.");
        return msg;
    }

    public UserProfileResponse getUserProfile(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found."));
        return new UserProfileResponse(user);
    }

}
