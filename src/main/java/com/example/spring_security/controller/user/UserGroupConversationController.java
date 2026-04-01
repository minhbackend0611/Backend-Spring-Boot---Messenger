package com.example.spring_security.controller.user;

import com.example.spring_security.dto.request.*;
import com.example.spring_security.dto.response.ListGroupConversationMessageResponse;
import com.example.spring_security.dto.response.SendMessageResponse;
import com.example.spring_security.dto.response.UserFriendResponse;
import com.example.spring_security.entities.User;
import com.example.spring_security.services.user.UserGroupConversationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat/user/group-conversations")
public class UserGroupConversationController {

        private final UserGroupConversationService userGroupConversationService;

        @PostMapping("")
        public ResponseEntity<Map<String, Object>> createGroup(@AuthenticationPrincipal User user,
                        @RequestBody CreateGroupRequest createGroupRequest) {
                return ResponseEntity
                                .ok(userGroupConversationService.createGroup(user.getUserId(), createGroupRequest));
        }

        @GetMapping("/{id}/group-conversation-messages")
        public ResponseEntity<ListGroupConversationMessageResponse> getMessages(
                        @AuthenticationPrincipal User user,
                        @PathVariable("id") Long groupConversationId,
                        @RequestParam(value = "cursorId", required = false) Long cursorId,
                        @RequestParam(value = "newerCursorId", required = false) Long newerCursorId,
                        @RequestParam(value = "jumpToMessageId", required = false) Long jumpToMessageId) {
                return ResponseEntity.ok(userGroupConversationService.getMessages(user.getUserId(), groupConversationId,
                                cursorId, newerCursorId, jumpToMessageId));
        }

        @PostMapping("/{id}/group-conversation-messages")
        public ResponseEntity<SendMessageResponse> sendMessage(
                        @AuthenticationPrincipal User user,
                        @PathVariable("id") Long groupConversationId,
                        @Valid @RequestBody SendMessageRequest sendMessageRequest) {
                return ResponseEntity.ok(userGroupConversationService
                                .sendMessage(user.getUserId(), groupConversationId, sendMessageRequest));
        }

        @PostMapping("/{id}/clear-history")
        public ResponseEntity<Map<String, String>> clearGroupHistory(
                        @AuthenticationPrincipal User user,
                        @PathVariable("id") Long groupConversationId) {

                return ResponseEntity.ok(
                                userGroupConversationService.clearGroupChatHistory(user.getUserId(),
                                                groupConversationId));
        }

        @DeleteMapping("{groupConversationId}/group-conversation-messages/{groupConversationMessageId}")
        public ResponseEntity<Map<String, String>> removeMessage(
                        @AuthenticationPrincipal User user,
                        @PathVariable("groupConversationId") Long groupConversationId,
                        @PathVariable("groupConversationMessageId") Long groupConversationMessageId,
                        @RequestParam(value = "isAll", required = true) Boolean isAll) {
                return ResponseEntity.ok(userGroupConversationService.removeMessage(
                                user.getUserId(), groupConversationId, groupConversationMessageId, isAll));
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Map<String, String>> removeConversation(
                        @AuthenticationPrincipal User user,
                        @PathVariable("id") Long groupConversationId) {
                return ResponseEntity.ok(userGroupConversationService
                                .removeConversation(user.getUserId(), groupConversationId));
        }

        @PutMapping("/{id}/groupName")
        public ResponseEntity<Map<String, String>> renameConversation(
                        @AuthenticationPrincipal User user,
                        @PathVariable("id") Long groupConversationId,
                        @RequestBody RenameGroupRequest renameGroupRequest) {
                return ResponseEntity.ok(userGroupConversationService
                                .renameConversation(user.getUserId(), groupConversationId, renameGroupRequest));
        }

        @PutMapping("{id}/avatar")
        public ResponseEntity<Map<String, String>> updateAvatar(
                        @AuthenticationPrincipal User user,
                        @PathVariable("id") Long groupConversationId,
                        @RequestParam("avatar") MultipartFile avatar) {
                return ResponseEntity.ok(userGroupConversationService.updateAvatar(user.getUserId(),
                                groupConversationId, avatar));
        }

        @GetMapping("{id}/list-add-members")
        public ResponseEntity<List<UserFriendResponse>> list(
                        @AuthenticationPrincipal User user,
                        @PathVariable("id") Long groupConversationId,
                        @RequestParam(value = "keyword", required = false) String keyword) {
                return ResponseEntity.ok(
                                userGroupConversationService.list(user.getUserId(), groupConversationId, keyword));
        }

        @PostMapping("{id}/members")
        public ResponseEntity<Map<String, String>> add(
                        @AuthenticationPrincipal User user,
                        @PathVariable("id") Long groupConversationId,
                        @RequestBody ModifyGroupMemberRequest modifyGroupMemberRequest) {
                return ResponseEntity.ok(
                                userGroupConversationService.addMembers(user.getUserId(), groupConversationId,
                                                modifyGroupMemberRequest));
        }

        @DeleteMapping("{id}/members")
        public ResponseEntity<Map<String, String>> remove(
                        @AuthenticationPrincipal User user,
                        @PathVariable("id") Long groupConversationId,
                        @RequestBody ModifyGroupMemberRequest modifyGroupMemberRequest) {
                return ResponseEntity.ok(
                                userGroupConversationService.removeMembers(user.getUserId(), groupConversationId,
                                                modifyGroupMemberRequest));
        }

        @PutMapping("{id}/members")
        public ResponseEntity<Map<String, String>> modifyRole(
                        @AuthenticationPrincipal User user,
                        @PathVariable("id") Long groupConversationId,
                        @Valid @RequestBody ModifyRoleRequest modifyRoleRequest) {
                return ResponseEntity.ok(
                                userGroupConversationService.modifyRoleMembers(user.getUserId(), groupConversationId,
                                                modifyRoleRequest));
        }

        @DeleteMapping("{id}/me")
        public ResponseEntity<Map<String, String>> out(
                        @AuthenticationPrincipal User user,
                        @PathVariable("id") Long groupConversationId) {
                return ResponseEntity.ok(
                                userGroupConversationService.out(user.getUserId(), groupConversationId));
        }

}
