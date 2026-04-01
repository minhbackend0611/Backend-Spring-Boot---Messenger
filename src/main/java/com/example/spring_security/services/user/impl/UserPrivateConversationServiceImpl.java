package com.example.spring_security.services.user.impl;

import com.example.spring_security.dto.request.SendMessageRequest;
import com.example.spring_security.dto.response.*;
import com.example.spring_security.entities.*;
import com.example.spring_security.entities.Enum.RealTimeAction;
import com.example.spring_security.exception.CustomException;
import com.example.spring_security.repository.*;
import com.example.spring_security.services.user.UserPrivateConversationService;
import com.example.spring_security.websocket.WebSocketPrivateMessageService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserPrivateConversationServiceImpl implements UserPrivateConversationService {

        private final WebSocketPrivateMessageService webSocketMessageService;

        private final PrivateConversationRepository privateConversationRepository;

        private final EntityManager entityManager;

        private final DeletePrivateConversationRepository deletePrivateConversationRepository;

        private final PrivateConversationMessageRepository privateConversationMessageRepository;

        private final DeletePrivateConversationMessageRepository deletePrivateConversationMessageRepository;

        private final UserRepository userRepository;

        public Map<String, String> create(Long userId1, Long userId2) {

                PrivateConversation privateConversation = PrivateConversation.builder()
                                .user1(entityManager.getReference(User.class, userId1))
                                .user2(entityManager.getReference(User.class, userId2))
                                .createdAt(LocalDateTime.now())
                                .previewMessage(null)
                                .build();

                privateConversationRepository.save(privateConversation);

                Map<String, String> msg = new HashMap<>();

                msg.put("message", "Created private conversation successfully");

                return msg;
        }

        public Map<String, String> removeConversation(Long removerId, Long privateConversationId) {

                PrivateConversation privateConversation = privateConversationRepository.findById(privateConversationId)
                                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND,
                                                "This conversation no longer exists."));

                DeletePrivateConversationId id = DeletePrivateConversationId.builder()
                                .userId(removerId)
                                .privateConversationId(privateConversationId)
                                .build();

                DeletePrivateConversation deletePrivateConversation = deletePrivateConversationRepository.findById(id)
                                .orElse(null);

                if (deletePrivateConversation == null)
                        deletePrivateConversation = DeletePrivateConversation.builder()
                                        .id(id)
                                        .deletedAt(LocalDateTime.now())
                                        .build();
                else
                        deletePrivateConversation.setDeletedAt(LocalDateTime.now());

                deletePrivateConversationRepository.save(deletePrivateConversation);

                privateConversation.setPreviewMessage(null);

                privateConversationRepository.save(privateConversation);

                Map<String, String> msg = new HashMap<>();
                msg.put("message", "Removed successfully!");
                return msg;
        }

        public Map<String, String> clearPrivateChatHistory(Long userId, Long privateConversationId) {
                PrivateConversation pc = privateConversationRepository.findById(privateConversationId)
                                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Conversation not found"));

                // Update the timestamp for the specific user
                if (pc.getUser1().getUserId().equals(userId)) {
                        pc.setUser1ClearedAt(LocalDateTime.now());
                } else if (pc.getUser2().getUserId().equals(userId)) {
                        pc.setUser2ClearedAt(LocalDateTime.now());
                } else {
                        throw new CustomException(HttpStatus.FORBIDDEN, "You are not part of this conversation");
                }

                privateConversationRepository.save(pc);
                return Map.of("message", "History cleared successfully");
        }

        public SendMessageResponse sendMessage(Long senderId,
                        Long privateConversationId,
                        SendMessageRequest sendMessageRequest) {

                PrivateConversation privateConversation = privateConversationRepository
                                .findById(privateConversationId).orElseThrow(
                                                () -> new CustomException(HttpStatus.NOT_FOUND,
                                                                "Illegal behavior. There is no conversation."));

                PrivateConversationMessage privateConversationMessage = PrivateConversationMessage.builder()
                                .privateConversation(entityManager.getReference(PrivateConversation.class,
                                                privateConversationId))
                                .sender(entityManager.getReference(User.class, senderId))
                                .sentAt(LocalDateTime.now())
                                .type(sendMessageRequest.getType())
                                .content(sendMessageRequest.getContent())
                                .build();

                privateConversationMessageRepository.save(privateConversationMessage);

                privateConversation.setPreviewMessage(privateConversationMessage);

                privateConversationRepository.save(privateConversation);

                // building for sender's info

                User sender = userRepository.findById(senderId).orElseThrow(
                                () -> new CustomException(HttpStatus.NOT_FOUND, "Sender not found"));

                PrivateMessageWsResponse privateMessageWsResponse = PrivateMessageWsResponse.builder()
                                .userId(senderId)
                                .username(sender.getUsername())
                                .firstName(sender.getFirstName())
                                .lastName(sender.getLastName())
                                .avatarUrl(sender.getAvatarUrl())
                                .privateConversationMessageId(
                                                privateConversationMessage.getPrivateConversationMessageId())
                                .privateConversationId(privateConversationId)
                                .content(privateConversationMessage.getContent())
                                .sentAt(privateConversationMessage.getSentAt())
                                .updatedAt(privateConversationMessage.getUpdatedAt())
                                .type(privateConversationMessage.getType())
                                .realTimeAction(RealTimeAction.SEND)
                                .build();

                // push for receiver

                User receiver = !privateConversation.getUser1().getUserId().equals(senderId)
                                ? privateConversation.getUser1()
                                : privateConversation.getUser2();

                webSocketMessageService.sendMessageToUser(receiver.getUsername(), privateMessageWsResponse);

                return SendMessageResponse.builder()
                                .messageId(privateConversationMessage.getPrivateConversationMessageId())
                                .content(privateConversationMessage.getContent())
                                .sentAt(privateConversationMessage.getSentAt())
                                .updatedAt(privateConversationMessage.getUpdatedAt())
                                .type(privateConversationMessage.getType())
                                .build();
        }

        public Map<String, String> removeMessage(Long userId, Long privateConversationId,
                        Long privateConversationMessageId, boolean isAll) {

                PrivateConversation privateConversation = privateConversationRepository.findById(privateConversationId)
                                .orElseThrow(
                                                () -> new CustomException(HttpStatus.NOT_FOUND,
                                                                "This conversation no longer exists."));

                PrivateConversationMessage privateConversationMessage = privateConversationMessageRepository
                                .findById(privateConversationMessageId).orElseThrow(
                                                () -> new CustomException(HttpStatus.NOT_FOUND,
                                                                "This message is no longer exists."));

                if (privateConversationMessage.getPrivateConversation()
                                .getPrivateConversationId() != privateConversationId) {
                        throw new CustomException(HttpStatus.BAD_REQUEST, "Mismatch conversation and message.");
                } else if (userId != privateConversation.getUser1().getUserId()
                                && userId != privateConversation.getUser2().getUserId()) {
                        throw new CustomException(HttpStatus.FORBIDDEN,
                                        "Illegal behavior. This user is not allowed to perform any actions to this conversation.");
                } else if (privateConversationMessage.getSender().getUserId() != userId && isAll) {
                        throw new CustomException(HttpStatus.CONFLICT,
                                        "Illegal behavior. This user is not allowed to remove both sides.");
                }

                DeletePrivateConversationMessageId deletePrivateConversationMessageId = DeletePrivateConversationMessageId
                                .builder()
                                .userId(userId)
                                .privateConversationMessageId(privateConversationMessageId)
                                .build();

                DeletePrivateConversationMessage deletePrivateConversationMessage = deletePrivateConversationMessageRepository
                                .findById(deletePrivateConversationMessageId).orElse(null);

                if (deletePrivateConversationMessage != null)
                        throw new CustomException(HttpStatus.CONFLICT,
                                        "Illegal behavior. This message is already removed.");

                deletePrivateConversationMessage = DeletePrivateConversationMessage.builder()
                                .id(deletePrivateConversationMessageId)
                                .deletedAt(LocalDateTime.now())
                                .isAll(isAll)
                                .user(userRepository.findById(userId).orElseThrow(
                                                () -> new CustomException(HttpStatus.NOT_FOUND,
                                                                "User no longer exists.")))
                                .build();

                deletePrivateConversationMessageRepository.save(deletePrivateConversationMessage);

                if (isAll) {

                        User partner = !privateConversation.getUser1().getUserId().equals(userId)
                                        ? privateConversation.getUser1()
                                        : privateConversation.getUser2();

                        User remover = userRepository.findById(userId).orElseThrow(
                                        () -> new CustomException(HttpStatus.NOT_FOUND, "Remover not found."));

                        DeletePrivateMessageWsResponse deletePrivateMessageWsResponse = DeletePrivateMessageWsResponse
                                        .builder()
                                        .userId(remover.getUserId())
                                        .privateConversationMessageId(
                                                        privateConversationMessage.getPrivateConversationMessageId())
                                        .privateConversationId(privateConversation.getPrivateConversationId())
                                        .realTimeAction(RealTimeAction.DELETE)
                                        .build();

                        webSocketMessageService.sendDeleteMessage(partner.getUsername(),
                                        deletePrivateMessageWsResponse);

                }

                Map<String, String> msg = new HashMap<>();

                msg.put("message", "Removed successfully.");

                return msg;

        }

        public ListPrivateConversationMessageResponse getMessages(Long userId, Long privateConversationId,
                        Long cursorId, Long newerCursorId, Long jumpToMessageId) {
                PrivateConversation privateConversation = privateConversationRepository
                                .findById(privateConversationId).orElseThrow(
                                                () -> new CustomException(HttpStatus.NOT_FOUND,
                                                                "Illegal behaivor. This conversation no longer exists."));

                LocalDateTime clearTime;
                if (privateConversation.getUser1().getUserId().equals(userId)) {
                        clearTime = privateConversation.getUser1ClearedAt();
                } else {
                        clearTime = privateConversation.getUser2ClearedAt();
                }

                if (clearTime == null) {
                        clearTime = LocalDateTime.of(1970, 1, 1, 0, 0);
                }

                List<PrivateConversationMessageResponse> messages;

                if (jumpToMessageId != null) {
                        List<PrivateConversationMessageResponse> older = privateConversationMessageRepository
                                        .findMessagesBeforeInclusive(userId, privateConversationId, jumpToMessageId,
                                                        clearTime, PageRequest.of(0, 25));

                        List<PrivateConversationMessageResponse> newer = privateConversationMessageRepository
                                        .findMessagesAfter(userId, privateConversationId, jumpToMessageId, clearTime,
                                                        PageRequest.of(0, 25));

                        java.util.Collections.reverse(newer);
                        messages = new java.util.ArrayList<>(newer);
                        messages.addAll(older);
                } else if (newerCursorId != null) {
                        messages = privateConversationMessageRepository
                                        .findMessagesAfter(userId, privateConversationId, newerCursorId,
                                                        clearTime,
                                                        PageRequest.of(0, 50));

                        java.util.Collections.reverse(messages);
                } else {
                        messages = privateConversationMessageRepository
                                        .findMessagesAfterTimestamp(
                                                        userId,
                                                        privateConversationId,
                                                        cursorId,
                                                        clearTime,
                                                        org.springframework.data.domain.PageRequest.of(0, 50));
                }

                User user = userId == privateConversation.getUser1().getUserId()
                                ? privateConversation.getUser2()
                                : privateConversation.getUser1();

                ListPrivateConversationMessageResponse listPrivateConversationMessageResponse = ListPrivateConversationMessageResponse
                                .builder()
                                .privateConversationMessageResponseList(messages)
                                .userId(user.getUserId())
                                .username(user.getUsername())
                                .firstName(user.getFirstName())
                                .lastName(user.getLastName())
                                .avatarUrl(user.getAvatarUrl())
                                .isOnline(user.getIsOnline())
                                .privateConversationId(privateConversationId)
                                .build();

                return listPrivateConversationMessageResponse;
        }

        public Map<String, Long> getIdConv(Long userId, Long friendId) {

                PrivateConversation privateConversation = privateConversationRepository
                                .findByUser1UserIdAndUser2UserId(userId, friendId)
                                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND,
                                                "There is no conversation."));

                return Map.of("privateConversationId", privateConversation.getPrivateConversationId());
        }

}
