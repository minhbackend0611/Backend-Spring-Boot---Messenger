package com.example.spring_security.controller.user;

import com.example.spring_security.dto.response.BaseUserResponse;
import com.example.spring_security.dto.response.MessageSearchResponse;
import com.example.spring_security.dto.response.UserSearchResponse;
import com.example.spring_security.entities.User;
import com.example.spring_security.services.user.UserSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat/user/search")
public class UserSearchController {

    private final UserSearchService userSearchService;

    @GetMapping("")
    public ResponseEntity<List<UserSearchResponse>> search
            (@AuthenticationPrincipal User user,
            @RequestParam(value = "keyword", required = true) String keyword) {
        return ResponseEntity.ok(userSearchService.search(user.getUserId(), keyword));
    }


    @GetMapping("/{id}/private-conversation-messages")
    public ResponseEntity<List<MessageSearchResponse>> searchPrivateMessages
            (@AuthenticationPrincipal User user, @PathVariable("id") Long privateConversationId,
             @RequestParam(value = "keyword", required = true) String keyword) {
        return ResponseEntity.ok(userSearchService.searchPrivateMessage(user.getUserId(), privateConversationId, keyword));
    }

    @GetMapping("/{id}/group-conversation-messages")
    public ResponseEntity<List<MessageSearchResponse>> searchGroupMessages
            (@PathVariable("id") Long groupConversationId,
             @AuthenticationPrincipal User user,
             @RequestParam(value = "keyword", required = true) String keyword) {
        return ResponseEntity.ok(userSearchService.searchGroupMessages(user.getUserId(), groupConversationId, keyword));
    }


    @GetMapping("/conversation-messages")
    public ResponseEntity<List<MessageSearchResponse>> searchAllMessages
            (@AuthenticationPrincipal User user,
             @RequestParam(value = "keyword", required = true) String keyword) {
        return ResponseEntity.ok(userSearchService.searchAllMessages(user.getUserId(), keyword));
    }
}
