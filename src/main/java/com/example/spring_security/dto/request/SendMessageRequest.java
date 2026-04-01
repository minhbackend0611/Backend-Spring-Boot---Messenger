package com.example.spring_security.dto.request;

import com.example.spring_security.entities.Enum.MessageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SendMessageRequest {

    @NotBlank(message = "The content must not be blank.")
    @Size(max = 3000, message = "The content is not allowed to exceed more than 3000 characters.")
    private String content;

    @NotNull(message = "The message type must not be null.")
    private MessageType type;

}
