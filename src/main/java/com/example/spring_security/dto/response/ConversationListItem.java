package com.example.spring_security.dto.response;

import com.example.spring_security.entities.Enum.ConversationType;
import com.example.spring_security.entities.Enum.MessageType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class ConversationListItem {
    private Long id;
    private ConversationType conversationType;

    private String name;
    private String avatarUrl;

    private String previewContent;
    private LocalDateTime previewTime;
    private MessageType messageType;

    public ConversationListItem(
            Short type,
            Long id,
            String name,
            String avatarUrl,
            String previewContent,
            Short previewType,
            java.sql.Timestamp previewTime
    ) {
        this.id = id;
        this.name = name;
        this.avatarUrl = avatarUrl;
        this.previewContent = previewContent;

        this.previewTime = previewTime != null ? previewTime.toLocalDateTime() : null;

        ConversationType.Converter converterConversationType  = new ConversationType.Converter();

        this.conversationType = converterConversationType.fromShort(type);

        MessageType.Converter converterMsgType = new MessageType.Converter();
        this.messageType = converterMsgType.fromShort(previewType);
    }
}

