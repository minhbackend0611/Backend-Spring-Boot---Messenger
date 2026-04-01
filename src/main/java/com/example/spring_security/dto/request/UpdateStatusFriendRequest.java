package com.example.spring_security.dto.request;


import com.example.spring_security.entities.Enum.FriendRequestStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
public class UpdateStatusFriendRequest {
    private Long senderId;
    private Long receiverId;
    private LocalDateTime sentAt;
    private FriendRequestStatus status;
}
