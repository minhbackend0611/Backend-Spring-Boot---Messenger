package com.example.spring_security.services.third.impl;

import com.example.spring_security.entities.Enum.Gender;
import com.example.spring_security.entities.Enum.Role;
import com.example.spring_security.entities.User;
import com.example.spring_security.repository.UserRepository;
import com.example.spring_security.services.third.InitializationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InitializationServiceImpl implements InitializationService {
    private final UserRepository userRepository;

    public void initAdmin() {
        User user = userRepository.findByRole(Role.ADMIN).orElse(null);
        if (user == null) {
            user = new User();
            user.setUsername("admin123");
            user.setEmail("admin123@gmail.com");
            user.setFirstName("User");
            user.setLastName("Super");
            user.setRole(Role.ADMIN);
            user.setHashPassword(new BCryptPasswordEncoder().encode("admin123"));
            user.setIsAccepted(true);
            user.setIsActive(true);
            user.setIsOnline(false);
            user.setAvatarUrl("");
            user.setAddress("");
            user.setJoinedAt(LocalDateTime.now());
            user.setGender(Gender.HIDDEN);
            user.setFriendCount(0);
            userRepository.save(user);
        }
    }
}
