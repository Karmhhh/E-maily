package com.example.Emaily.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.Store;

import com.example.Emaily.Component.EmailServiceImpl;
import com.example.Emaily.Model.EmailObj;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.*;

@Service
public class EmailService {

    @Autowired
    private StatefulRedisConnection<String, String> connection;

    @Autowired
    private EmailServiceImpl emailService;

    @Autowired
    private Folder inbox;

    @Autowired
    private Store store;

    public String inboxMessage() throws Exception {

        // Recupera i messaggi
        Message[] messages = inbox.getMessages();

        // Converti i messaggi in una lista di oggetti JSON
        List<Map<String, Object>> emails = new ArrayList<>();
        for (Message message : messages) {
            Map<String, Object> emailData = new HashMap<>();
            emailData.put("from", Arrays.toString(message.getFrom()));
            emailData.put("subject", message.getSubject());
            emailData.put("content", message.getContent().toString());
            emailData.put("to", Arrays.toString(message.getAllRecipients()));
            emails.add(emailData);
        }

        // Converti la lista di email in JSON
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(emails);

        // Chiudi la cartella "inbox" e lo store
        inbox.close(false);
        store.close();
        return json;
    }

    public String saveEmail(EmailObj obj) {

        RedisCommands<String, String> syncCommands = connection.sync();

        syncCommands.hset("Email_" + obj.getEmailId(), "Email_UUUID", obj.getEmailId().toString());
        syncCommands.hset("Email_" + obj.getEmailId(), "Mittente", obj.getMittente());
        syncCommands.hset("Email_" + obj.getEmailId(), "Destinatario", obj.getDestinatario());
        syncCommands.hset("Email_" + obj.getEmailId(), "CCs", obj.getCCs().toString());
        syncCommands.hset("Email_" + obj.getEmailId(), "Email_Object", obj.getEmailObject());
        syncCommands.hset("Email_" + obj.getEmailId(), "Email_Body", obj.getEmailBody());
        syncCommands.hset("Email_" + obj.getEmailId(), "Date_Send", obj.getDateSend());
        syncCommands.hset("Email_" + obj.getEmailId(), "State", obj.isState().toString());

        // Converti l'oggetto EmailObj in formato JSON utilizzando Jackson
        ObjectMapper mapper = new ObjectMapper();
        String jsonRecord;
        try {
            jsonRecord = mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            // Gestione dell'eccezione, ad esempio logger.error("Errore durante la
            // conversione in JSON", e);
            jsonRecord = "{}"; // Se non riesci a convertire, restituisci un JSON vuoto o un messaggio di
                               // errore
        }
        emailService.sendSimpleMessage(obj);

        return jsonRecord;
    }

}
//Esempio di riferimento di come gestire i messaggi presi dalla cartella inbox

    // public void fetchAndProcessEmails() throws MessagingException, IOException {

    // Message[] messages = inbox.getMessages();
    // for (Message message : messages) {
    // String subject = message.getSubject();
    // String from = InternetAddress.toString(message.getFrom());
    // String content = message.getContent().toString();
    // System.out.println("Subject: " + subject);
    // System.out.println("From: " + from);
    // System.out.println("Content: " + content);
    // }

    // inbox.close(false);
    // store.close();
    // }
