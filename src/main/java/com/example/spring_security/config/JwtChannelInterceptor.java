package com.example.spring_security.config;

import com.example.spring_security.repository.UserRepository;
import com.example.spring_security.services.third.JWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {


    private final JWTService jwtService;

    private final UserRepository userRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {


        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            List<String> authHeader = accessor.getNativeHeader("Authorization");

            if (authHeader != null && !authHeader.isEmpty()) {

                String token = authHeader.get(0).replace("Bearer ", "");

                String username = jwtService.extractUsername(token);

                UserDetails userDetails = userRepository.findByUsername(username).orElse(null);

                if (jwtService.isTokenValid(token, userDetails))
                    if (userDetails != null) {
                        accessor.setUser(new UsernamePasswordAuthenticationToken(username, null, userDetails.getAuthorities()));
                    }

            }
        }
        return message;
    }
}

