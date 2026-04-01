package com.example.spring_security.services.user.impl;

import com.example.spring_security.dto.request.UpdateStatusFriendRequest;
import com.example.spring_security.dto.response.FriendRequestResponse;
import com.example.spring_security.dto.response.ListUserFriendResponse;
import com.example.spring_security.dto.response.NotificationWsResponse;
import com.example.spring_security.dto.response.UserFriendResponse;
import com.example.spring_security.entities.*;
import com.example.spring_security.entities.Enum.FriendRequestStatus;
import com.example.spring_security.exception.CustomException;
import com.example.spring_security.repository.*;
import com.example.spring_security.services.user.UserFriendService;
import com.example.spring_security.websocket.WebSocketNotificationService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserFriendServiceImpl implements UserFriendService {
    private final WebSocketNotificationService webSocketNotificationService;

    private final UserRepository userRepository;

    private final FriendRepository friendRepository;

    private final FriendRequestRepository friendRequestRepository;

    private final BlockRepository blockRepository;

    private final UserPrivateConversationServiceImpl userPrivateConversationService;

    private final PrivateConversationRepository privateConversationRepository;

    public ListUserFriendResponse getFriendList(Long userId, String keyword) {
        if (keyword == null)
            keyword = "";
        List<UserFriendResponse> userFriendResponses = friendRepository.findAllFriendsByUserIdAndKeywordOrderBy(userId,
                keyword, "isOnline");
        ListUserFriendResponse listUserFriendResponse = new ListUserFriendResponse(
                userFriendResponses,
                friendRepository.countFriends(userId, keyword));
        return listUserFriendResponse;
    }

    public Map<String, String> friendRequest(Long senderId, Long receiverId) {

        User sender = userRepository.findById(senderId).orElse(null);

        User receiver = userRepository.findById(receiverId).orElse(null);

        Map<String, String> msg = new HashMap<>();

        if (sender == null || receiver == null) {
            throw new RuntimeException("User not found.");
        }

        if (sender.getUserId() == receiverId) {
            throw new RuntimeException("Illegal behavior.");
        }

        if (blockRepository.existsBlockBetween(senderId, receiverId))
            throw new CustomException(HttpStatus.CONFLICT, "Action forbidden: users have blocked each other.");

        Friend friend = friendRepository.findExistingFriendBetween(sender.getUserId(), receiverId).orElse(null);

        if (friend != null) {
            throw new CustomException(HttpStatus.CONFLICT, "You are already friends.");
        }

        FriendRequest friendRequest = friendRequestRepository
                .findActiveFriendRequestsBetweenUsers(sender.getUserId(), receiverId)
                .stream()
                .findFirst().orElse(null);

        if (friendRequest == null || (friendRequest.getReceiver().getUserId() == sender.getUserId()
                && friendRequest.getStatus() == FriendRequestStatus.REJECTED)) {
            if (friendRequest != null) {
                friendRequest.setActive(false);
                friendRequestRepository.save(friendRequest);
            }
            FriendRequestId friendRequestId = FriendRequestId.builder()
                    .senderId(sender.getUserId())
                    .receiverId(receiver.getUserId())
                    .sentAt(LocalDateTime.now())
                    .build();
            friendRequest = FriendRequest.builder().id(friendRequestId).sender(sender).receiver(receiver)
                    .status(FriendRequestStatus.PENDING)
                    .isActive(true)
                    .updatedAt(null).build();
            friendRequestRepository.save(friendRequest);
            msg.put("message", friendRequestId.getSentAt().toString());

            webSocketNotificationService.sendToUser(
                    receiver.getUsername(),
                    NotificationWsResponse.builder()
                            .type("FRIEND_REQUEST_RECEIVED")
                            .fromUserId(sender.getUserId())
                            .fromUsername(sender.getUsername())
                            .build());

            return msg;
        } else {
            if (friendRequest.getSender().getUserId() == sender.getUserId()) {
                if (friendRequest.getStatus() == FriendRequestStatus.PENDING
                        || friendRequest.getStatus() == FriendRequestStatus.REJECTED) {
                    msg.put("message",
                            "You have already sent a friend request to this user. Please wait for a response.");
                    return msg;
                }
            } else {
                if (friendRequest.getStatus() == FriendRequestStatus.PENDING) {
                    msg.put("message",
                            "This person has already sent you a friend request. Please check your pending requests.");
                    return msg;
                }
            }
        }
        msg.put("message", "Something went wrong. Please try again.");
        return msg;
    }

    public List<FriendRequestResponse> getListFriendRequestReceive(Long id, String keyword) {
        List<FriendRequest> listFriendRequest = friendRequestRepository.findPendingRequestsReceived(id, keyword);
        List<FriendRequestResponse> listFriendRequestResponse = listFriendRequest.stream()
                .map(f -> new FriendRequestResponse(f.getSender(), f.getId().getSentAt()))
                .collect(Collectors.toList());
        return listFriendRequestResponse;
    }

    public List<FriendRequestResponse> getListFriendRequestSent(Long id, String keyword) {
        List<FriendRequest> listFriendRequest = friendRequestRepository.findPendingRequestsSent(id, keyword);
        List<FriendRequestResponse> listFriendRequestResponse = listFriendRequest.stream()
                .map(f -> new FriendRequestResponse(f.getReceiver(), f.getId().getSentAt()))
                .collect(Collectors.toList());
        return listFriendRequestResponse;
    }

    private void makeFriend(Long userId1, Long userId2) {
        FriendId friendId = FriendId.builder().userId1(userId1).userId2(userId2).build();

        User user1 = userRepository.findById(userId1).orElseThrow(() -> new RuntimeException("User not found."));

        User user2 = userRepository.findById(userId2).orElseThrow(() -> new RuntimeException("User not found."));

        Friend friend = Friend.builder().id(friendId)
                .user1Entity(user1)
                .user2Entity(user2)
                .madeFriendAt(LocalDateTime.now())
                .build();

        user1.setFriendCount(user1.getFriendCount() + 1);

        user2.setFriendCount(user2.getFriendCount() + 1);

        userRepository.save(user1);
        userRepository.save(user2);
        friendRepository.save(friend);
    }

    public Map<String, String> updateStatus(UpdateStatusFriendRequest updateStatusFriendRequest, Long updatorId) {

        Map<String, String> msg = new HashMap<>();

        User updator = userRepository.findById(updatorId)
                .orElseThrow(() -> new RuntimeException("User no longer exists."));

        FriendRequestId friendRequestId = FriendRequestId.builder()
                .senderId(updateStatusFriendRequest.getSenderId())
                .receiverId(updateStatusFriendRequest.getReceiverId())
                .sentAt(updateStatusFriendRequest.getSentAt())
                .build();
        FriendRequest friendRequest = friendRequestRepository.findById(friendRequestId).orElse(null);
        if (friendRequest == null || friendRequest.getStatus() == FriendRequestStatus.CANCELLED
                || !friendRequest.isActive()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "This request is no longer available.");
        } else {
            if ((friendRequest.getSender().getUserId() == updator.getUserId()
                    && updateStatusFriendRequest.getStatus() != FriendRequestStatus.CANCELLED)
                    ||
                    (friendRequest.getReceiver().getUserId() == updator.getUserId()
                            && updateStatusFriendRequest.getStatus() == FriendRequestStatus.CANCELLED)) {
                throw new CustomException(HttpStatus.CONFLICT, "Illegal behavior up to sender or receiver.");
            }

            String moreInfo = "";
            String type = "";
            String targetUsername = "";
            Long fromUserId = -1L;
            String fromUsername = "";

            if (updateStatusFriendRequest.getStatus() == FriendRequestStatus.ACCEPTED) {
                friendRequest.setActive(false);
                moreInfo = " You are already friends.";
                Long userId1 = Math.min(friendRequest.getSender().getUserId(), friendRequest.getReceiver().getUserId());
                Long userId2 = Math.max(friendRequest.getSender().getUserId(), friendRequest.getReceiver().getUserId());

                // make friend

                makeFriend(userId1, userId2);

                // create private conversation

                userPrivateConversationService.create(userId1, userId2);

                type = "FRIEND_REQUEST_ACCEPTED";

                targetUsername = friendRequest.getSender().getUsername();
                fromUserId = friendRequest.getReceiver().getUserId();
                fromUsername = friendRequest.getReceiver().getUsername();

            } else if (updateStatusFriendRequest.getStatus() == FriendRequestStatus.REJECTED) {
                moreInfo = " You have rejected.";
                type = "FRIEND_REQUEST_REJECTED";

                targetUsername = friendRequest.getSender().getUsername();
                fromUserId = friendRequest.getReceiver().getUserId();
                fromUsername = friendRequest.getReceiver().getUsername();
            } else if (updateStatusFriendRequest.getStatus() == FriendRequestStatus.CANCELLED) {
                moreInfo = " You have cancelled.";
                friendRequest.setActive(false);
                type = "FRIEND_REQUEST_CANCELLED";

                targetUsername = friendRequest.getReceiver().getUsername();
                fromUserId = friendRequest.getSender().getUserId();
                fromUsername = friendRequest.getSender().getUsername();
            }
            friendRequest.setStatus(updateStatusFriendRequest.getStatus());
            friendRequestRepository.save(friendRequest);
            msg.put("message", "Update friend request status successfully." + moreInfo);

            webSocketNotificationService.sendToUser(
                    friendRequest.getReceiver().getUsername(),
                    NotificationWsResponse.builder()
                            .type(type)
                            .fromUserId(friendRequest.getSender().getUserId())
                            .fromUsername(friendRequest.getSender().getUsername())
                            .build());

            return msg;
        }
    }

    public Map<String, String> removeFriend(Long removerId, Long removedUserId) {

        Map<String, String> msg = new HashMap<>();

        User remover = userRepository.findById(removerId)
                .orElseThrow(() -> new RuntimeException("User no longer exists."));

        Friend friend = friendRepository.findExistingFriendBetween(remover.getUserId(), removedUserId)
                .orElse(null);
        if (friend == null) {
            msg.put("message", "You are no longer friends.");
            return msg;
        }

        PrivateConversation privateConversation = privateConversationRepository
                .findByUser1UserIdAndUser2UserId(removerId, removedUserId).orElse(null);

        if (privateConversation != null)
            privateConversationRepository.delete(privateConversation);

        friendRepository.deleteById(friend.getId());

        User user1 = friend.getUser1Entity();

        User user2 = friend.getUser2Entity();

        user1.setFriendCount(user1.getFriendCount() - 1);

        user2.setFriendCount(user2.getFriendCount() - 1);

        userRepository.save(user1);

        userRepository.save(user2);

        msg.put("message", "You have successfully unfriended this user.");

        webSocketNotificationService.sendToUser(
                user2.getUsername(), // the removed user
                NotificationWsResponse.builder()
                        .type("FRIEND_REMOVED")
                        .fromUserId(removerId)
                        .fromUsername(remover.getUsername())
                        .build());

        return msg;

    }
}
