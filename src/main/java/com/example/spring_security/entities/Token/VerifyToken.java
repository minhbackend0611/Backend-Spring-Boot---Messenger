package com.example.spring_security.entities.Token;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table (name = "verify_token")
public class VerifyToken extends BaseToken {
    @Column(name = "expired_at")
    private LocalDateTime expiredAt;
}