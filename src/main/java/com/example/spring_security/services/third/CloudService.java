package com.example.spring_security.services.third;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface CloudService {
    String uploadAvatar(MultipartFile file, String username) throws IOException;
    String uploadGroupAvatars(MultipartFile file, String username) throws IOException;
}
