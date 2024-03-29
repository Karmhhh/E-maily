package com.example.Emaily.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Store;

@Configuration
public class MailStoreConfiguration {
    @Value("${session.auth}")
    String sessionAuth;
    @Value("${protocol}")
    String protocol;
    @Value("${store.host}")
    String storeHost;
    @Value("${session.host}")
    String sessionHost;
    @Value("${session.port}")
    String sessionPort;
    @Value("${session.starttls}")
    String sessionstarttls;

    @Autowired
    private Session mailSession;

    @Bean
    public Store mailStore() throws MessagingException {
        Store store = mailSession.getStore(protocol);
        store.connect(storeHost, "b734cbf3569f73", "c003eedf13a28e");
        return store;
    }
}