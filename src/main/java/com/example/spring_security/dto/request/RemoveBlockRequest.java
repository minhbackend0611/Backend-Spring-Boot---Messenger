package com.example.spring_security.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RemoveBlockRequest {
    private Long blockerId;
    private Long blockedUserId;
    private LocalDateTime blockedAt;
}
