package com.example.spring_security.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "record_signin")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecordSignIn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_signin_id")
    Long recordSignInId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    User user;

    @Column(name = "signed_in_at")
    LocalDateTime signedInAt;

    @Column(name = "is_successful")
    Boolean isSuccessful;
}
