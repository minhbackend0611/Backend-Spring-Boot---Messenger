package com.example.spring_security.services.third.impl;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.spring_security.services.third.CloudService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudServiceImpl implements CloudService {

    private final Cloudinary cloudinary;

    public CloudServiceImpl() {
        cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dh7nm63dk",
                "api_key", "696229857485276",
                "api_secret", "nh_akt-YdqdNjuoST4aOwgeFKWY"
        ));
    }

    public String uploadAvatar(MultipartFile file, String username) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap(
                        "folder", "avatars",
                        "public_id", username
                ));
        return uploadResult.get("secure_url").toString(); // URL avatar
    }

    public String uploadGroupAvatars(MultipartFile file, String username) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap(
                        "folder", "group_avatars",
                        "public_id", username
                ));
        return uploadResult.get("secure_url").toString(); // URL avatar
    }
}

