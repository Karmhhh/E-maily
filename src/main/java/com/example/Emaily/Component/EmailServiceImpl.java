package com.example.Emaily.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.example.Emaily.Model.EmailObj;

@Component
public class EmailServiceImpl {

    @Autowired
    private JavaMailSender emailSender;

    public void sendSimpleMessage(EmailObj obj) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(obj.getMittente());
        message.setTo(obj.getDestinatario());
        message.setSubject(obj.getEmailObject());
        message.setText(obj.getEmailBody());
        message.setCc(obj.getCCs().toArray(new String[obj.getCCs().size()]));
    
        emailSender.send(message);
    }
}