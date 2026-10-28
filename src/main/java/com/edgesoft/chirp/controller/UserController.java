package com.edgesoft.chirp.controller;


import com.edgesoft.chirp.dto.RegisterRequest;
import com.edgesoft.chirp.dto.RegisterResponse;
import com.edgesoft.chirp.dto.UserResponse;
import com.edgesoft.chirp.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/email")
    public ResponseEntity<UserResponse> getUserByEmail(@RequestParam("email") String emailId){
        return ResponseEntity.ok(userService.getUserByEmail(emailId));
    }

    @GetMapping("/id")
    public ResponseEntity<UserResponse> getUserById(@RequestParam("uuid") UUID uuid){
        return ResponseEntity.ok(userService.getUserById(uuid));
    }

    // User registration
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> registerUser(@RequestBody @Valid RegisterRequest registerRequest){
        //field validations, existing user, encryption, persistent storage
        log.info("User registration is in progress");
        return ResponseEntity.ok(userService.register(registerRequest));

    }

}
