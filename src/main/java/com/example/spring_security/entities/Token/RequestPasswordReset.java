package com.example.spring_security.entities.Token;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "request_password_reset")
@AttributeOverride(name = "id", column = @Column(name = "request_id"))
@AttributeOverride(name = "createdAt", column = @Column(name = "requested_at"))
public class RequestPasswordReset extends BaseToken {}
