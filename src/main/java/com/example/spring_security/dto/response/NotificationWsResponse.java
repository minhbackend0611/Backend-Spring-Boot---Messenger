package com.example.spring_security.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationWsResponse {
    private String type;
    private Integer count;
    private Long fromUserId;
    private String fromUsername;
}
