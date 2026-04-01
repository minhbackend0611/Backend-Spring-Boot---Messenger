package com.example.spring_security.services.user.impl;

import com.example.spring_security.dto.request.*;
import com.example.spring_security.dto.response.*;
import com.example.spring_security.entities.*;
import com.example.spring_security.entities.Enum.GroupRole;
import com.example.spring_security.entities.Enum.MessageType;
import com.example.spring_security.entities.Enum.RealTimeAction;
import com.example.spring_security.exception.CustomException;
import com.example.spring_security.repository.*;
import com.example.spring_security.services.third.CloudService;
import com.example.spring_security.services.user.UserGroupConversationService;
import com.example.spring_security.websocket.WebSocketGroupMessageService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserGroupConversationServiceImpl implements UserGroupConversationService {

        private final WebSocketGroupMessageService webSocketGroupMessageService;

        private final UserRepository userRepository;

        private final GroupConversationRepository groupConversationRepository;

        private final GroupConversationMemberRepository groupConversationMemberRepository;

        private final GroupConversationMessageRepository groupConversationMessageRepository;

        private final EntityManager entityManager;

        private final FriendRepository friendRepository;

        @Autowired
        private SimpMessagingTemplate messagingTemplate;

        private final DeleteGroupConversationRepository deleteGroupConversationRepository;

        private final DeleteGroupConversationMessageRepository deleteGroupConversationMessageRepository;

        private final CloudService cloudService;

        public Map<String, Object> createGroup(Long userId, CreateGroupRequest createGroupRequest) {

                if (createGroupRequest.getUserIds().contains(userId)) {
                        throw new CustomException(HttpStatus.CONFLICT,
                                        "Illegal behavior. Admin must not be included in member list.");
                }

                createGroupRequest.getUserIds().forEach(
                                id -> friendRepository.findExistingFriendBetween(userId, id).orElseThrow(
                                                () -> new CustomException(HttpStatus.CONFLICT,
                                                                "You are no longer friends.")));

                GroupConversation groupConversation = new GroupConversation();

                groupConversation.setOwner(entityManager.getReference(User.class, userId));

                groupConversation.setCreatedAt(LocalDateTime.now());

                groupConversation.setIsEncrypted(false);

                // System.out.println("HERE");

                StringBuilder stringBuilder = new StringBuilder();

                if (createGroupRequest.getGroupName() == null || createGroupRequest.getGroupName().isEmpty()) {

                        String name = userRepository.findById(userId)
                                        .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND,
                                                        "User no longer exists."))
                                        .getFirstName();

                        stringBuilder.append(name);

                        for (int i = 0; i < createGroupRequest.getUserIds().size(); i++) {
                                User user = userRepository.findById(createGroupRequest.getUserIds().get(i))
                                                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND,
                                                                "User no longer exists."));
                                name = user.getFirstName();
                                stringBuilder.append(", " + name);
                        }

                        groupConversation.setGroupName(stringBuilder.toString());

                }

                else
                        groupConversation.setGroupName(createGroupRequest.getGroupName());

                groupConversationRepository.save(groupConversation);

                GroupConversationMemberId groupConversationMemberId = GroupConversationMemberId.builder()
                                .groupConversationId(groupConversation.getGroupConversationId())
                                .memberId(userId)
                                .build();

                GroupConversationMember admin = GroupConversationMember.builder()
                                .id(groupConversationMemberId)
                                .member(entityManager.getReference(User.class, userId))
                                .groupConversation(entityManager.getReference(GroupConversation.class,
                                                groupConversation.getGroupConversationId()))
                                .groupRole(GroupRole.ADMIN)
                                .joinedAt(LocalDateTime.now())
                                .appointedAt(LocalDateTime.now())
                                .build();

                groupConversationMemberRepository.save(admin);

                for (int i = 0; i < createGroupRequest.getUserIds().size(); i++) {
                        User user = userRepository.findById(createGroupRequest.getUserIds().get(i))
                                        .orElseThrow(
                                                        () -> new CustomException(HttpStatus.NOT_FOUND,
                                                                        "User no longer exists."));
                        groupConversationMemberId = GroupConversationMemberId.builder()
                                        .groupConversationId(groupConversation.getGroupConversationId())
                                        .memberId(user.getUserId())
                                        .build();

                        GroupConversationMember member = GroupConversationMember.builder()
                                        .id(groupConversationMemberId)
                                        .member(entityManager.getReference(User.class, user.getUserId()))
                                        .groupConversation(entityManager.getReference(GroupConversation.class,
                                                        groupConversation.getGroupConversationId()))
                                        .groupRole(GroupRole.MEMBER)
                                        .joinedAt(LocalDateTime.now())
                                        .appointedAt(null)
                                        .build();

                        groupConversationMemberRepository.save(member);

                }

                Map<String, Object> response = new HashMap<>();

                response.put("message", "Created group successfully.");

                response.put("groupConversationId", groupConversation.getGroupConversationId());

                return response;

        }

        public ListGroupConversationMessageResponse getMessages(Long userId, Long groupConversationId, Long cursorId,
                        Long newerCursorId,
                        Long jumpToMessageId) {

                GroupConversation groupConversation = groupConversationRepository.findById(groupConversationId)
                                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND,
                                                "This conversation no longer exists."));

                GroupConversationMember groupConversationMember = groupConversationMemberRepository
                                .findMemberInGroup(userId, groupConversationId)
                                .orElseThrow(
                                                () -> new CustomException(HttpStatus.FORBIDDEN,
                                                                "This user is not allowed to perform this action."));

                LocalDateTime clearTime = groupConversationMember.getHistoryClearedAt();
                if (clearTime == null) {
                        clearTime = LocalDateTime.of(1970, 1, 1, 0, 0);
                }

                List<GroupMemberResponse> groupMemberResponseList = groupConversationMemberRepository
                                .findMembersByGroupConversationId(groupConversationId);

                List<GroupConversationMessageResponse> groupConversationMessageResponseList;

                if (jumpToMessageId != null) {
                        List<GroupConversationMessageResponse> older = groupConversationMessageRepository
                                        .findMessagesBeforeInclusive(userId, groupConversationId, jumpToMessageId,
                                                        clearTime, PageRequest.of(0, 25));

                        // 2. Get Newer (25 messages) - Revert list to make them ascending
                        List<GroupConversationMessageResponse> newer = groupConversationMessageRepository
                                        .findMessagesAfter(userId, groupConversationId, jumpToMessageId, clearTime,
                                                        PageRequest.of(0, 25));

                        java.util.Collections.reverse(newer);
                        groupConversationMessageResponseList = new java.util.ArrayList<>(newer);
                        groupConversationMessageResponseList.addAll(older);
                } else if (newerCursorId != null) {
                        groupConversationMessageResponseList = groupConversationMessageRepository
                                        .findMessagesAfter(userId, groupConversationId, newerCursorId,
                                                        clearTime,
                                                        PageRequest.of(0, 50));

                        java.util.Collections.reverse(groupConversationMessageResponseList);
                } else {
                        groupConversationMessageResponseList = groupConversationMessageRepository
                                        .findMessagesAfterTimestamp(userId, groupConversationId, cursorId, clearTime,
                                                        org.springframework.data.domain.PageRequest.of(0, 50));
                }

                ListGroupConversationMessageResponse listGroupConversationMessageResponse = ListGroupConversationMessageResponse
                                .builder()
                                .avatarUrl(groupConversation.getAvatarUrl())
                                .groupName(groupConversation.getGroupName())
                                .isEncrypted(groupConversation.getIsEncrypted())
                                .groupConversationId(groupConversationId)
                                .groupMemberResponseList(groupMemberResponseList)
                                .groupConversationMessageResponseList(groupConversationMessageResponseList)
                                .build();

                return listGroupConversationMessageResponse;

        }

        public SendMessageResponse sendMessage(Long userId,
                        Long groupConversationId,
                        SendMessageRequest sendMessageRequest) {

                GroupConversation groupConversation = groupConversationRepository.findById(groupConversationId)
                                .orElseThrow(
                                                () -> new CustomException(HttpStatus.NOT_FOUND,
                                                                "Illegal behavior. There is no conversation."));

                GroupConversationMember groupConversationMember = groupConversationMemberRepository
                                .findMemberInGroup(userId, groupConversationId)
                                .orElseThrow(
                                                () -> new CustomException(HttpStatus.FORBIDDEN,
                                                                "This user is not allowed to perform this action."));

                System.out.println(sendMessageRequest.getContent());

                GroupConversationMessage groupConversationMessage = GroupConversationMessage.builder()
                                .sender(entityManager.getReference(User.class, userId))
                                .groupConversation(entityManager.getReference(GroupConversation.class,
                                                groupConversationId))
                                .content(sendMessageRequest.getContent())
                                .sentAt(LocalDateTime.now())
                                .updatedAt(null)
                                .type(sendMessageRequest.getType())
                                .build();

                System.out.println("Before error.");

                groupConversationMessageRepository.save(groupConversationMessage);

                groupConversation.setPreviewMessage(entityManager
                                .getReference(GroupConversationMessage.class,
                                                groupConversationMessage.getGroupConversationMessageId()));

                groupConversationRepository.save(groupConversation);

                User sender = userRepository.findById(userId)
                                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "User not found"));

                GroupMessageWsResponse groupMessageWsResponse = GroupMessageWsResponse.builder()
                                .senderId(userId)
                                .senderName(sender.getFirstName() + " " + sender.getLastName())
                                .avatarUrl(sender.getAvatarUrl())
                                .groupConversationId(groupConversationId)
                                .groupConversationMessageId(groupConversationMessage.getGroupConversationMessageId())
                                .content(sendMessageRequest.getContent())
                                .type(sendMessageRequest.getType())
                                .sentAt(groupConversationMessage.getSentAt())
                                .realTimeAction(RealTimeAction.SEND)
                                .updatedAt(null)
                                .build();

                webSocketGroupMessageService.sendGroupMessage(groupConversationId, groupMessageWsResponse);

                return SendMessageResponse.builder()
                                .messageId(groupConversationMessage.getGroupConversationMessageId())
                                .content(groupConversationMessage.getContent())
                                .sentAt(groupConversationMessage.getSentAt())
                                .updatedAt(groupConversationMessage.getUpdatedAt())
                                .type(groupConversationMessage.getType())
                                .build();

        }

        public Map<String, String> removeMessage(Long userId, Long groupConversationId, Long groupConversationMessageId,
                        Boolean isAll) {

                GroupConversation groupConversation = groupConversationRepository.findById(groupConversationId)
                                .orElseThrow(
                                                () -> new CustomException(HttpStatus.NOT_FOUND,
                                                                "This conversation no longer exists."));

                GroupConversationMessage groupConversationMessage = groupConversationMessageRepository
                                .findById(groupConversationMessageId).orElseThrow(
                                                () -> new CustomException(HttpStatus.NOT_FOUND,
                                                                "This message is no longer exists."));

                GroupConversationMember groupConversationMember = groupConversationMemberRepository
                                .findMemberInGroup(userId, groupConversationId)
                                .orElseThrow(
                                                () -> new CustomException(HttpStatus.FORBIDDEN,
                                                                "This user is not allowed to perform this action."));

                if (groupConversationMessage.getGroupConversation().getGroupConversationId() != groupConversationId) {
                        throw new CustomException(HttpStatus.BAD_REQUEST, "Mismatch conversation and message.");
                } else if (groupConversationMessage.getSender().getUserId() != userId && isAll) {
                        throw new CustomException(HttpStatus.CONFLICT,
                                        "Illegal behavior. This user is not allowed to remove both sides.");
                }

                DeleteGroupConversationMessageId deleteGroupConversationMessageId = DeleteGroupConversationMessageId
                                .builder()
                                .memberId(userId)
                                .groupConversationMessageId(groupConversationMessageId)
                                .build();

                DeleteGroupConversationMessage deleteGroupConversationMessage = deleteGroupConversationMessageRepository
                                .findById(deleteGroupConversationMessageId).orElse(null);

                if (deleteGroupConversationMessage != null)
                        throw new CustomException(HttpStatus.CONFLICT,
                                        "Illegal behavior. This message is already removed.");

                deleteGroupConversationMessage = DeleteGroupConversationMessage.builder()
                                .id(deleteGroupConversationMessageId)
                                .deletedAt(LocalDateTime.now())
                                .isAll(isAll)
                                .member(userRepository.findById(userId).orElseThrow(
                                                () -> new CustomException(HttpStatus.NOT_FOUND,
                                                                "User no longer exists.")))
                                .build();

                deleteGroupConversationMessageRepository.save(deleteGroupConversationMessage);

                DeleteGroupMessageWsResponse deleteGroupMessageWsResponse = DeleteGroupMessageWsResponse.builder()
                                .userId(userId)
                                .groupConversationMessageId(groupConversationMessageId)
                                .groupConversationId(groupConversation.getGroupConversationId())
                                .realTimeAction(RealTimeAction.DELETE)
                                .isAll(deleteGroupConversationMessage.getIsAll())
                                .build();

                if (isAll)
                        webSocketGroupMessageService.sendDeleteGroupMessage(groupConversationId,
                                        deleteGroupMessageWsResponse);

                groupConversation.setPreviewMessage(null);

                groupConversationRepository.save(groupConversation);

                Map<String, String> msg = new HashMap<>();

                msg.put("message", "Removed successfully.");

                return msg;

        }

        public Map<String, String> clearGroupChatHistory(Long userId, Long groupConversationId) {

                // 1. Find the Member
                GroupConversationMember member = groupConversationMemberRepository
                                .findMemberInGroup(userId, groupConversationId)
                                .orElseThrow(() -> new CustomException(HttpStatus.FORBIDDEN,
                                                "You are not a member of this group."));

                // 2. Set the "Floor" timestamp to NOW
                // (User will only see messages sent AFTER this time)
                member.setHistoryClearedAt(LocalDateTime.now());

                groupConversationMemberRepository.save(member);

                return Map.of("message", "Chat history cleared successfully.");
        }

        public Map<String, String> removeConversation(Long removerId, Long groupConversationId) {

                GroupConversationMember groupConversationMember = groupConversationMemberRepository
                                .findMemberInGroup(removerId, groupConversationId)
                                .orElseThrow(
                                                () -> new CustomException(HttpStatus.FORBIDDEN,
                                                                "This user is not allowed to perform this action."));

                DeleteGroupConversationId id = DeleteGroupConversationId.builder()
                                .memberId(removerId)
                                .groupConversationId(groupConversationId)
                                .build();

                DeleteGroupConversation deleteGroupConversation = deleteGroupConversationRepository.findById(id)
                                .orElse(null);

                if (deleteGroupConversation == null)
                        deleteGroupConversation = DeleteGroupConversation.builder()
                                        .id(id)
                                        .deletedAt(LocalDateTime.now())
                                        .build();
                else
                        deleteGroupConversation.setDeletedAt(LocalDateTime.now());

                deleteGroupConversationRepository.save(deleteGroupConversation);

                Map<String, String> msg = new HashMap<>();
                msg.put("message", "Removed successfully!");
                return msg;
        }

        public Map<String, String> renameConversation(Long userId, Long groupConversationId,
                        RenameGroupRequest renameGroupRequest) {

                GroupConversation groupConversation = groupConversationRepository.findById(groupConversationId)
                                .orElseThrow(
                                                () -> new CustomException(HttpStatus.NOT_FOUND,
                                                                "This group no longer exists."));

                GroupConversationMember groupConversationMember = groupConversationMemberRepository
                                .findMemberInGroup(userId, groupConversationId)
                                .orElseThrow(
                                                () -> new CustomException(HttpStatus.FORBIDDEN,
                                                                "This user is not allowed to perform this action."));

                groupConversation.setGroupName(renameGroupRequest.getGroupName());

                groupConversationRepository.save(groupConversation);

                GroupMessageWsResponse groupMessageWsResponse = GroupMessageWsResponse.builder()
                                .groupConversationId(groupConversationId)
                                .senderId(userId)
                                .content(renameGroupRequest.getGroupName()) // <--- Payload is the new name
                                .type(MessageType.SYSTEM) // Mark as system message
                                .realTimeAction(RealTimeAction.UPDATE) // <--- Action type
                                .build();

                webSocketGroupMessageService.sendGroupMessage(groupConversationId, groupMessageWsResponse);

                Map<String, String> msg = new HashMap<>();

                msg.put("groupName", groupConversation.getGroupName());

                return msg;

        }

        public Map<String, String> updateAvatar(Long userId, Long groupConversationId, MultipartFile avatar) {
                GroupConversation groupConversation = groupConversationRepository.findById(groupConversationId)
                                .orElseThrow(
                                                () -> new CustomException(HttpStatus.NOT_FOUND,
                                                                "This group no longer exists."));

                GroupConversationMember groupConversationMember = groupConversationMemberRepository
                                .findMemberInGroup(userId, groupConversationId)
                                .orElseThrow(
                                                () -> new CustomException(HttpStatus.FORBIDDEN,
                                                                "This user is not allowed to perform this action."));

                if (avatar.isEmpty())
                        throw new CustomException(HttpStatus.BAD_REQUEST, "There is no avatar to set.");
                else {
                        try {
                                String avatarUrl = cloudService.uploadGroupAvatars(avatar,
                                                groupConversation.getGroupConversationId().toString());
                                groupConversation.setAvatarUrl(avatarUrl);
                                groupConversationRepository.save(groupConversation);

                                GroupMessageWsResponse groupMessageWsResponse = GroupMessageWsResponse.builder()
                                                .groupConversationId(groupConversationId)
                                                .senderId(userId)
                                                .content(avatarUrl) // <--- Payload is the new URL
                                                .type(MessageType.SYSTEM)
                                                .realTimeAction(RealTimeAction.UPDATE)
                                                .build();

                                webSocketGroupMessageService.sendGroupMessage(groupConversationId,
                                                groupMessageWsResponse);

                                Map<String, String> msg = new HashMap<>();

                                msg.put("avatarUrl", avatarUrl);

                                return msg;
                        } catch (Exception e) {
                                throw new CustomException(HttpStatus.BAD_REQUEST,
                                                "Something went wrong. Unable to set avatar.");
                        }
                }
        }

        public List<UserFriendResponse> list(Long userId, Long groupConversationId, String keyword) {
                GroupConversation groupConversation = groupConversationRepository.findById(groupConversationId)
                                .orElseThrow(
                                                () -> new CustomException(HttpStatus.NOT_FOUND,
                                                                "This conversation no longer exists."));

                GroupConversationMember groupConversationMember = groupConversationMemberRepository
                                .findMemberInGroup(userId, groupConversationId)
                                .orElseThrow(
                                                () -> new CustomException(HttpStatus.FORBIDDEN,
                                                                "This user is not allowed to perform this action."));

                if (keyword == null)
                        keyword = "";

                List<UserFriendResponse> basicUserResponseList = friendRepository
                                .findFriendsToAddGroup(userId, groupConversationId, keyword);

                return basicUserResponseList;
        }

        public Map<String, String> addMembers(Long userId, Long groupConversationId,
                        ModifyGroupMemberRequest modifyGroupMemberRequest) {
                GroupConversation groupConversation = groupConversationRepository.findById(groupConversationId)
                                .orElseThrow(
                                                () -> new CustomException(HttpStatus.NOT_FOUND,
                                                                "This conversation no longer exists."));

                GroupConversationMember groupConversationMember = groupConversationMemberRepository
                                .findMemberInGroup(userId, groupConversationId)
                                .orElseThrow(
                                                () -> new CustomException(HttpStatus.FORBIDDEN,
                                                                "This user is not allowed to perform this action."));

                List<Long> memberIds = groupConversationMemberRepository.findAllMemberIdsOf(groupConversationId);

                boolean alreadyIn = modifyGroupMemberRequest.getMemberIds().stream().anyMatch(
                                memberIds::contains);

                if (alreadyIn)
                        throw new CustomException(HttpStatus.CONFLICT, "There is any members already in the group.");

                for (int i = 0; i < modifyGroupMemberRequest.getMemberIds().size(); i++) {
                        Long newMemberId = modifyGroupMemberRequest.getMemberIds().get(i);
                        User user = userRepository.findById(modifyGroupMemberRequest.getMemberIds().get(i))
                                        .orElseThrow(
                                                        () -> new CustomException(HttpStatus.NOT_FOUND,
                                                                        "User no longer exists."));

                        Friend friend = friendRepository.findExistingFriendBetween(userId, user.getUserId())
                                        .orElseThrow(
                                                        () -> new CustomException(HttpStatus.CONFLICT,
                                                                        "You are no longer friends."));

                        GroupConversationMemberId id = GroupConversationMemberId.builder()
                                        .memberId(user.getUserId())
                                        .groupConversationId(groupConversationId)
                                        .build();

                        groupConversationMember = GroupConversationMember.builder()
                                        .id(id)
                                        .member(entityManager.getReference(User.class, userId))
                                        .groupConversation(entityManager.getReference(GroupConversation.class,
                                                        groupConversationId))
                                        .groupRole(GroupRole.MEMBER)
                                        .joinedAt(LocalDateTime.now())
                                        .appointedAt(null)
                                        .build();

                        groupConversationMemberRepository.save(groupConversationMember);

                        GroupMessageWsResponse welcomeMsg = GroupMessageWsResponse.builder()
                                        .groupConversationId(groupConversationId)
                                        .senderId(userId) // Admin ID
                                        .content("You have been added to the group.")
                                        .type(MessageType.SYSTEM)
                                        .realTimeAction(RealTimeAction.ADD_MEMBER)
                                        .build();

                        // Sends to: /user/{newMemberId}/client/messages
                        messagingTemplate.convertAndSendToUser(
                                        user.getUsername(),
                                        "/client/messages",
                                        welcomeMsg);
                }

                GroupMessageWsResponse systemMsg = GroupMessageWsResponse.builder()
                                .groupConversationId(groupConversationId)
                                .senderId(userId)
                                .content("New members added.")
                                .type(MessageType.SYSTEM)
                                .realTimeAction(RealTimeAction.UPDATE) // Reuse UPDATE to trigger refresh
                                .build();

                webSocketGroupMessageService.sendGroupMessage(groupConversationId, systemMsg);

                return Map.of("message", "Added successfully.");
        }

        public Map<String, String> removeMembers(Long userId, Long groupConversationId,
                        ModifyGroupMemberRequest modifyGroupMemberRequest) {
                GroupConversation groupConversation = groupConversationRepository.findById(groupConversationId)
                                .orElseThrow(
                                                () -> new CustomException(HttpStatus.NOT_FOUND,
                                                                "This conversation no longer exists."));

                GroupConversationMember groupConversationMember = groupConversationMemberRepository
                                .findMemberInGroup(userId, groupConversationId)
                                .orElseThrow(
                                                () -> new CustomException(HttpStatus.FORBIDDEN,
                                                                "This user is not allowed to perform this action."));

                List<Long> memberIds = groupConversationMemberRepository.findAllMemberIdsOf(groupConversationId);

                boolean allIn = modifyGroupMemberRequest.getMemberIds().stream().allMatch(
                                memberIds::contains);

                if (!allIn)
                        throw new CustomException(HttpStatus.CONFLICT, "There is any members not in the group.");

                if (modifyGroupMemberRequest.getMemberIds().contains(userId))
                        throw new CustomException(HttpStatus.CONFLICT, "You can not remove yourself.");

                if (!groupConversationMember.getGroupRole().equals(GroupRole.ADMIN))
                        throw new CustomException(HttpStatus.FORBIDDEN, "You are not allowed to perform this action.");

                for (int i = 0; i < modifyGroupMemberRequest.getMemberIds().size(); i++) {
                        Long memberToRemoveId = modifyGroupMemberRequest.getMemberIds().get(i);

                        GroupConversationMemberId id = GroupConversationMemberId.builder()
                                        .memberId(modifyGroupMemberRequest.getMemberIds().get(i))
                                        .groupConversationId(groupConversationId)
                                        .build();

                        groupConversationMemberRepository.deleteById(id);

                        GroupMessageWsResponse kickMsg = GroupMessageWsResponse.builder()
                                        .groupConversationId(groupConversationId)
                                        .senderId(userId) // Admin who kicked
                                        .content(String.valueOf(memberToRemoveId)) // <--- Payload is the Kicked User ID
                                        .type(MessageType.SYSTEM)
                                        .realTimeAction(RealTimeAction.KICK_MEMBER)
                                        .build();

                        webSocketGroupMessageService.sendGroupMessage(groupConversationId, kickMsg);

                }

                return Map.of("message", "Removed successfully.");
        }

        public Map<String, String> modifyRoleMembers(Long userId, Long groupConversationId,
                        ModifyRoleRequest modifyRoleRequest) {

                GroupConversation groupConversation = groupConversationRepository.findById(groupConversationId)
                                .orElseThrow(
                                                () -> new CustomException(HttpStatus.NOT_FOUND,
                                                                "This conversation no longer exists."));

                GroupConversationMember groupConversationMember = groupConversationMemberRepository
                                .findMemberInGroup(userId, groupConversationId)
                                .orElseThrow(
                                                () -> new CustomException(HttpStatus.FORBIDDEN,
                                                                "You are not allowed to perform this action."));

                List<Long> memberIds = groupConversationMemberRepository.findAllMemberIdsOf(groupConversationId);

                if (!GroupRole.ADMIN.equals(groupConversationMember.getGroupRole()))
                        throw new CustomException(HttpStatus.FORBIDDEN, "You are not allowed to perform this action.");
                if (!memberIds.contains(modifyRoleRequest.getMemberId()))
                        throw new CustomException(HttpStatus.BAD_REQUEST, "This user is not in the group.");

                groupConversationMember = groupConversationMemberRepository.findMemberInGroup(
                                modifyRoleRequest.getMemberId(), groupConversationId).orElseThrow(
                                                () -> new CustomException(HttpStatus.NOT_FOUND,
                                                                "This user is already removed."));

                GroupRole.Converter converter = new GroupRole.Converter();

                groupConversationMember.setGroupRole(converter.fromShort(modifyRoleRequest.getGroupRole()));

                groupConversationMemberRepository.save(groupConversationMember);

                GroupMessageWsResponse roleMsg = GroupMessageWsResponse.builder()
                                .groupConversationId(groupConversationId)
                                .senderId(userId) // Admin who changed it
                                .content("Role updated for user " + modifyRoleRequest.getMemberId())
                                .type(MessageType.SYSTEM)
                                .realTimeAction(RealTimeAction.ROLE_CHANGE) // Ensure Enum has this!
                                .build();

                webSocketGroupMessageService.sendGroupMessage(groupConversationId, roleMsg);

                Map<String, String> msg = new HashMap<>();

                msg.put("message", "Modified role successfully");

                return msg;

        }

        public Map<String, String> out(Long userId, Long groupConversationId) {

                GroupConversation groupConversation = groupConversationRepository.findById(groupConversationId)
                                .orElseThrow(
                                                () -> new CustomException(HttpStatus.NOT_FOUND,
                                                                "This conversation no longer exists."));

                GroupConversationMember groupConversationMember = groupConversationMemberRepository
                                .findMemberInGroup(userId, groupConversationId)
                                .orElseThrow(
                                                () -> new CustomException(HttpStatus.FORBIDDEN,
                                                                "You are not allowed to perform this action."));

                groupConversationMemberRepository.deleteById(GroupConversationMemberId.builder()
                                .memberId(userId)
                                .groupConversationId(groupConversationId)
                                .build());

                GroupMessageWsResponse leaveMsg = GroupMessageWsResponse.builder()
                                .groupConversationId(groupConversationId)
                                .senderId(userId)
                                .content("A member has left the group.")
                                .type(MessageType.SYSTEM)
                                .realTimeAction(RealTimeAction.UPDATE)
                                .build();

                webSocketGroupMessageService.sendGroupMessage(groupConversationId, leaveMsg);

                List<GroupMemberResponse> groupMemberResponseList = groupConversationMemberRepository
                                .findMembersByGroupConversationId(groupConversationId);

                boolean noAdmin = groupMemberResponseList.stream().noneMatch(
                                member -> GroupRole.ADMIN.equals(member.getGroupRole()));

                if (noAdmin) {
                        groupConversationMember = groupConversationMemberRepository
                                        .findJoinedEarliest(groupConversationId)
                                        .orElse(null);
                        if (groupConversationMember == null)
                                groupConversationRepository.delete(groupConversation);
                        else {
                                groupConversationMember.setGroupRole(GroupRole.ADMIN);
                                groupConversationMemberRepository.save(groupConversationMember);
                        }
                }

                return Map.of("message", "Leaved successfully.");
        }

}
