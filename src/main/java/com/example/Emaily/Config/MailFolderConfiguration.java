package com.example.Emaily.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.mail.Folder;
import jakarta.mail.MessagingException;
import jakarta.mail.Store;

@Configuration
public class MailFolderConfiguration {
    
    @Autowired
    private Store mailStore;

    @Bean
    public Folder mailFolder() throws MessagingException {
        Folder folder = mailStore.getFolder("INBOX");
        folder.open(Folder.READ_ONLY);
        return folder;
    }
}