package com.example.spring_security.controller.user;

import com.example.spring_security.entities.User;
import com.example.spring_security.services.user.UserConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/chat/user/conversations")
@RequiredArgsConstructor
public class UserConversationController {

    private final UserConversationService userConversationService;

    @GetMapping("")
    public ResponseEntity<?> getAllConversation(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userConversationService.getConversationList(user.getUserId()));
    }
}
