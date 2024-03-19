package com.example.Emaily.Controller;

import com.example.Emaily.Model.UserObj;
import com.example.Emaily.Service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserControl {

    @Autowired
    UserService userService;

    @PostMapping("/newUser")
    public String addUser(@RequestBody UserObj entity) {
        return userService.saveUser(entity);
      
       }

}
