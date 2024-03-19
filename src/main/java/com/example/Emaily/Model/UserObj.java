package com.example.Emaily.Model;

import java.util.UUID;

import org.springframework.data.annotation.Id;

public class UserObj {
    @Id
    private UUID userId;
    private String name;
    private String address; 
    
    public UUID getUserId() {
        return userId;
    }
    public void setUserId(UUID userId) {
        this.userId = userId;
    }   
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
}
