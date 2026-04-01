package com.example.spring_security.controller.user;

import com.example.spring_security.dto.request.SendMessageRequest;
import com.example.spring_security.dto.response.ListPrivateConversationMessageResponse;
import com.example.spring_security.dto.response.MessageSearchResponse;
import com.example.spring_security.dto.response.SendMessageResponse;
import com.example.spring_security.entities.User;
import com.example.spring_security.services.user.UserPrivateConversationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat/user/private-conversations")
public class UserPrivateConversationController {

        private final UserPrivateConversationService userPrivateConversationService;

        @DeleteMapping("/{id}")
        public ResponseEntity<Map<String, String>> removeConversation(@AuthenticationPrincipal User user,
                        @PathVariable Long id) {
                return ResponseEntity.ok(
                                userPrivateConversationService.removeConversation(user.getUserId(), id));
        }

        @PostMapping("/{id}/private-conversation-messages")
        public ResponseEntity<SendMessageResponse> sendMessage(@AuthenticationPrincipal User sender,
                        @PathVariable("id") Long privateConversationId,
                        @Valid @RequestBody SendMessageRequest sendMessageRequest) {

                return ResponseEntity.ok(userPrivateConversationService
                                .sendMessage(sender.getUserId(), privateConversationId, sendMessageRequest));
        }

        @DeleteMapping("{privateConversationId}/private-conversation-messages/{privateConversationMessageId}")
        public ResponseEntity<Map<String, String>> removeMessage(@AuthenticationPrincipal User remover,
                        @PathVariable("privateConversationId") Long privateConversationId,
                        @PathVariable("privateConversationMessageId") Long privateConversationMessageId,
                        @RequestParam(value = "isAll", required = true) boolean isAll) {

                return ResponseEntity.ok(userPrivateConversationService.removeMessage(remover.getUserId(),
                                privateConversationId, privateConversationMessageId, isAll));
        }

        @PostMapping("/{id}/clear-history")
        public ResponseEntity<Map<String, String>> clearHistory(
                        @AuthenticationPrincipal User user,
                        @PathVariable("id") Long privateConversationId) {

                return ResponseEntity.ok(
                                userPrivateConversationService.clearPrivateChatHistory(user.getUserId(),
                                                privateConversationId));
        }

        @GetMapping("/{id}/private-conversation-messages")
        public ResponseEntity<ListPrivateConversationMessageResponse> getMessages(@AuthenticationPrincipal User user,
                        @PathVariable("id") Long privateConversationId,
                        @RequestParam(value = "cursorId", required = false) Long cursorId,
                        @RequestParam(value = "newerCursorId", required = false) Long newerCursorId,
                        @RequestParam(value = "jumpToMessageId", required = false) Long jumpToMessageId) {
                return ResponseEntity
                                .ok(userPrivateConversationService.getMessages(user.getUserId(), privateConversationId,
                                                cursorId, newerCursorId, jumpToMessageId));
        }

        @GetMapping("/{friendId}")
        public ResponseEntity<Map<String, Long>> getIdConv(@AuthenticationPrincipal User user,
                        @PathVariable(value = "friendId", required = true) Long friendId) {
                return ResponseEntity.ok(userPrivateConversationService.getIdConv(user.getUserId(), friendId));
        }

}
