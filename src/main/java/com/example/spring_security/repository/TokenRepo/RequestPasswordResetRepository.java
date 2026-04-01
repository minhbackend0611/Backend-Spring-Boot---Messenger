package com.example.spring_security.repository.TokenRepo;

import com.example.spring_security.entities.Token.RequestPasswordReset;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestPasswordResetRepository extends BaseTokenRepository<RequestPasswordReset> {}