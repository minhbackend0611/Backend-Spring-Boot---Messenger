package com.example.spring_security.repository.TokenRepo;

import com.example.spring_security.entities.Token.VerifyEmailChangeToken;
import org.springframework.stereotype.Repository;

@Repository
public interface VerifyEmailChangeTokenRepository extends BaseTokenRepository<VerifyEmailChangeToken> {}
