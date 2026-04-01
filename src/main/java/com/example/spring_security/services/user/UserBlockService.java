package com.example.spring_security.services.user;


import com.example.spring_security.dto.request.RemoveBlockRequest;
import com.example.spring_security.dto.response.UserBlockResponse;

import java.util.List;
import java.util.Map;

public interface UserBlockService {
    Map<String, String> blockRequest(Long blockerId, Long blockedUserId);
    List<UserBlockResponse> getBlockList(Long blockerId);
    Map<String, String> removeRequest(RemoveBlockRequest removeBlockRequest);
}
