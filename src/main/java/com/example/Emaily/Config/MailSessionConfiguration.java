package com.example.Emaily.Config;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.mail.Session;

@Configuration
public class MailSessionConfiguration {
    
    @Bean
    public Session mailSession() {
        Properties properties = new Properties();
        properties.setProperty("mail.pop3.host", "pop3.mailtrap.io");
        properties.setProperty("mail.pop3.port", "1100"); // Or "9950" if SSL is required
        properties.setProperty("mail.pop3.starttls.enable", "true");
        properties.setProperty("mail.pop3.auth", "true");

        return Session.getDefaultInstance(properties);
    }
}
