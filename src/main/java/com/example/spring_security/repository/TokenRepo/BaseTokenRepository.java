package com.example.spring_security.repository.TokenRepo;

import com.example.spring_security.entities.Token.BaseToken;
import com.example.spring_security.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

@NoRepositoryBean
public interface BaseTokenRepository<T extends BaseToken> extends JpaRepository<T, Long> {
    Optional<T> findByToken(String token);
    Optional<T> findByUser(User user);
}
