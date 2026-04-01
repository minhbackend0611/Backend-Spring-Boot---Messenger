package com.example.spring_security.dto.response;

import com.example.spring_security.entities.RecordSignIn;
import com.example.spring_security.entities.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class RecordSignInResponse {


    private Long recordSignInId;

    private Long userId;

    private String username;

    private String fullName;

    private LocalDateTime signedInAt;

    private Boolean isSuccessful;

    public RecordSignInResponse(Long recordSignInId, Boolean isSuccessful, java.sql.Timestamp signedInAt,
                                Long userId, String username, String fullName ) {
        this.recordSignInId = recordSignInId;
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.signedInAt = signedInAt != null ? signedInAt.toLocalDateTime() : null;
        this.isSuccessful = isSuccessful;
    }

}
