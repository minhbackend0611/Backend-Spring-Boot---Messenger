package com.example.spring_security.entities.Token;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;


@Entity
@Data
@Table(name = "verify_email_change_token")
public class VerifyEmailChangeToken extends BaseToken {
    @Column(name = "expired_at")
    private LocalDateTime expiredAt;
    @Column(name = "new_email")
    private String newEmail;
}
