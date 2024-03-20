package com.example.Emaily.Config;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.mail.Session;

@Configuration
public class MailSessionConfiguration {
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
    @Value("${session.portintgr}")
    String sessionportint;
    @Value("${session.starttls}")
    String sessionstarttls;

    private Logger LOG = LoggerFactory.getLogger(getClass());

    @Bean
    public Session mailSession() {
        
        Properties properties = new Properties();
        properties.setProperty(sessionHost, storeHost);
        properties.setProperty(sessionPort, sessionportint);
        properties.setProperty(sessionstarttls, "true");
        properties.setProperty(sessionAuth, "true");

        return Session.getDefaultInstance(properties);
    }
}
