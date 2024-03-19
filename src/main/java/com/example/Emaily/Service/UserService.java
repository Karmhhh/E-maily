package com.example.Emaily.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Emaily.Model.UserObj;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

@Service
public class UserService {

    @Autowired
    private StatefulRedisConnection<String, String> connection;

    public String saveUser(UserObj obj) {
        RedisCommands<String, String> syncCommands = connection.sync();
        syncCommands.hset("User_" + obj.getUserId(), "User_Id", obj.getUserId().toString());
        syncCommands.hset("User_" + obj.getUserId(), "Name", obj.getName());
        syncCommands.hset("User_" + obj.getUserId(), "Address", obj.getAddress());
        
        // Converti l'oggetto EmailObj in formato JSON utilizzando Jackson
        ObjectMapper mapper = new ObjectMapper();
        String jsonRecord;
        try {
            jsonRecord = mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            // Gestione dell'eccezione, ad esempio logger.error("Errore durante la conversione in JSON", e);
            jsonRecord = "{}"; // Se non riesci a convertire, restituisci un JSON vuoto o un messaggio di errore
        }
        
        return jsonRecord;
    }
}
