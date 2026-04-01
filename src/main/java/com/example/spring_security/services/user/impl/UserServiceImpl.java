package com.example.spring_security.services.user.impl;

import com.example.spring_security.repository.UserRepository;
import com.example.spring_security.services.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
                return userRepository.findByUsername(login)
                        .or(() -> userRepository.findByEmail(login))
                        .orElseThrow(() -> new UsernameNotFoundException("User not found with username or email: " + login));
            }
        };
    }
}
