package com.example.Emaily.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Store;

@Configuration
public class MailStoreConfiguration {

    @Autowired
    private Session mailSession;

    @Bean
    public Store mailStore() throws MessagingException {
        Store store = mailSession.getStore("pop3");
        store.connect("pop3.mailtrap.io", "b734cbf3569f73", "c003eedf13a28e");
        return store;
    }
}