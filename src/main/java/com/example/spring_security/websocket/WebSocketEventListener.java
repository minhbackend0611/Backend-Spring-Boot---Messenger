package com.example.spring_security.websocket;

import com.example.spring_security.dto.response.UserFriendResponse;
import com.example.spring_security.dto.response.UserSearchResponse;
import com.example.spring_security.entities.Friend;
import com.example.spring_security.entities.RecordOnlineUser;
import com.example.spring_security.entities.User;
import com.example.spring_security.exception.CustomException;
import com.example.spring_security.repository.FriendRepository;
import com.example.spring_security.repository.RecordOnlineUserRepository;
import com.example.spring_security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final UserRepository userRepository;

    private final FriendRepository friendRepository;

    private final WebSocketStatusService webSocketStatusService;

    private final RecordOnlineUserRepository recordOnlineUserRepository;

    @EventListener
    public void handleSessionConnected(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Authentication auth = (Authentication) accessor.getUser();
        if (auth != null) {
            User user = userRepository.findByUsername(auth.getName()).orElseThrow(
                    () -> new CustomException(HttpStatus.NOT_FOUND, "User not found.")
            );
            user.setIsOnline(true);
            userRepository.save(user);

            String sessionId = accessor.getSessionId();

            RecordOnlineUser recordOnlineUser = new RecordOnlineUser();
            recordOnlineUser.setSessionId(sessionId);
            recordOnlineUser.setUserId(user.getUserId());
            recordOnlineUser.setOnlineAt(LocalDateTime.now());

            recordOnlineUserRepository.save(recordOnlineUser);

            List<Friend> friends = friendRepository.findByIdUserId1OrIdUserId2(user.getUserId(), user.getUserId());

            friends.stream().forEach( f -> {

                String friendUsername = f.getId().getUserId1() != user.getUserId()
                        ? f.getUser1Entity().getUsername()
                        : f.getUser2Entity().getUsername();

                UserFriendResponse payloadStatus = UserFriendResponse.builder()
                        .userId(user.getUserId())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .avatarUrl(user.getAvatarUrl())
                        .username(user.getUsername())
                        .isOnline(user.getIsOnline())
                        .address(user.getAddress())
                        .madeFriendAt(f.getMadeFriendAt())
                        .build();

                webSocketStatusService.sendStatusToFriend(friendUsername, payloadStatus);

            });

        }
    }

    @EventListener
    public void handleSessionDisconnected(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Authentication auth = (Authentication) accessor.getUser();
        if (auth != null) {
            User user = userRepository.findByUsername(auth.getName()).orElseThrow(
                    () -> new CustomException(HttpStatus.NOT_FOUND, "User not found.")
            );
            user.setIsOnline(false);
            userRepository.save(user);

            String sessionId = accessor.getSessionId();

            RecordOnlineUser recordOnlineUser = recordOnlineUserRepository.findById(sessionId).orElse(null);

            if (recordOnlineUser != null) recordOnlineUser.setOfflineAt(LocalDateTime.now());

            recordOnlineUserRepository.save(recordOnlineUser);

            List<Friend> friends = friendRepository.findByIdUserId1OrIdUserId2(user.getUserId(), user.getUserId());

            friends.stream().forEach( f -> {

                String friendUsername = f.getId().getUserId1() != user.getUserId()
                        ? f.getUser1Entity().getUsername()
                        : f.getUser2Entity().getUsername();

                UserFriendResponse payloadStatus = UserFriendResponse.builder()
                        .userId(user.getUserId())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .avatarUrl(user.getAvatarUrl())
                        .username(user.getUsername())
                        .isOnline(user.getIsOnline())
                        .address(user.getAddress())
                        .madeFriendAt(f.getMadeFriendAt())
                        .build();

                webSocketStatusService.sendStatusToFriend(friendUsername, payloadStatus);
            });

        }
    }
}

