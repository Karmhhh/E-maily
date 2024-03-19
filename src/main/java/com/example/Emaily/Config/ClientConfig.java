package com.example.Emaily.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;

@Configuration
public class ClientConfig {
    // Variabile definita in application.properties che prende il path dove
    // stabilire la connessione
    @Value("${connection.variable}")
    private String connectionValueString;

    @Bean
    public StatefulRedisConnection redisConnection() {
        RedisClient redisClient = RedisClient.create(connectionValueString);
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        return connection;
    }
}