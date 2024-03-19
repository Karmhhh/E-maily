package com.example.Emaily.Controller;

import com.example.Emaily.Model.EmailObj;
import com.example.Emaily.Service.EmailService;

import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.mail.Store;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// {
// "emailId": "423b728e-830e-4d9a-819e-05e7d3cb6656",
// "mittente": "exempledev001@gmail.com",
// "destinatario": "carmelapia01@gmail.com",
// "emailObject": "Test",
// "emailBody": "questo e' il body.test.",
// "ccs": "[ ]",
// "dateSend": ""
// }

@RestController
@RequestMapping("/api/email")
public class EmailControl {

    @Autowired
    EmailService emailService;

    @Autowired
    Store store;

    @PostMapping("/newEmail")
    public String saveEmail(@RequestBody EmailObj obj) throws JsonProcessingException {
        return emailService.sendNewEmail(obj);
    }

    @GetMapping("/retriveMails")
    public String retriveMails() {
        try {
            return emailService.inboxMessage();
        } catch (Exception e) {
            e.printStackTrace();
            return "No Response";
        }
    }
}
