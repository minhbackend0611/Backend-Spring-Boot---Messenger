package com.example.spring_security.controller.user;

import com.example.spring_security.dto.request.UpdateStatusFriendRequest;
import com.example.spring_security.dto.response.FriendRequestResponse;
import com.example.spring_security.dto.response.ListUserFriendResponse;
import com.example.spring_security.entities.User;
import com.example.spring_security.services.user.UserFriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/chat/user/friend")
@RequiredArgsConstructor
public class UserFriendController {
    private final UserFriendService userFriendService;


    @GetMapping("/list")
    public ResponseEntity<ListUserFriendResponse> getFriendList(@AuthenticationPrincipal User user,
                                                                @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword) {
        return ResponseEntity.ok(userFriendService.getFriendList(user.getUserId(), keyword));
    }

    @PostMapping("/request/{id}")
    public ResponseEntity<Map<String, String>> friendRequest(@AuthenticationPrincipal User sender, @PathVariable("id") Long receiverId) {
        return ResponseEntity.ok(userFriendService.friendRequest(sender.getUserId(), receiverId));
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<Map<String, String>> removeFriend(@AuthenticationPrincipal User remover, @PathVariable("id") Long removedUserId) {
        return ResponseEntity.ok(userFriendService.removeFriend(remover.getUserId(), removedUserId));
    }

    @GetMapping("/list-request-received")
    public ResponseEntity<List<FriendRequestResponse>> getListFriendRequestReceived
            (@AuthenticationPrincipal User user,
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword) {
        return ResponseEntity.ok(userFriendService.getListFriendRequestReceive(user.getUserId(), keyword));
    }
    @GetMapping("/list-request-sent")
    public ResponseEntity<List<FriendRequestResponse>> getListFriendRequestSent
            (@AuthenticationPrincipal User user,
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword) {
        return ResponseEntity.ok(userFriendService.getListFriendRequestSent(user.getUserId(), keyword));
    }
    @PostMapping("/update-status")
    public ResponseEntity<Map<String, String>> updateStatus(@RequestBody UpdateStatusFriendRequest updateStatusFriendRequest,
                                                            @AuthenticationPrincipal User updator) {
        return ResponseEntity.ok(userFriendService.updateStatus(updateStatusFriendRequest, updator.getUserId()));
    }
}
