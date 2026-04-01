package com.example.spring_security.controller.user;


import com.example.spring_security.dto.request.RemoveBlockRequest;
import com.example.spring_security.dto.response.UserBlockResponse;
import com.example.spring_security.entities.User;
import com.example.spring_security.services.user.UserBlockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat/user/block")
@RequiredArgsConstructor
public class UserBlockController {
    private final UserBlockService userBlockService;

    @GetMapping("/list")
    public ResponseEntity<List<UserBlockResponse>> getBlockList(@AuthenticationPrincipal User blocker) {
        return ResponseEntity.ok(userBlockService.getBlockList(blocker.getUserId()));
    }

    @PostMapping("/request/{id}")
    public ResponseEntity<Map<String, String>> blockRequest(@AuthenticationPrincipal User blocker, @PathVariable("id") Long blockedUserId) {
        return ResponseEntity.ok(userBlockService.blockRequest(blocker.getUserId(), blockedUserId));
    }

    @DeleteMapping("/remove-block")
    public ResponseEntity<Map<String, String>> removeRequest(@RequestBody RemoveBlockRequest removeBlockRequest) {
        return ResponseEntity.ok(userBlockService.removeRequest(removeBlockRequest));
    }
}
