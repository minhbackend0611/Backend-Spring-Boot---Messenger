package com.example.spring_security.services.third;

public interface EmailService {
    void sendEmail(String to, String subject, String text);
}
