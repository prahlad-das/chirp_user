package com.edgesoft.chirp.controller;


import com.edgesoft.chirp.dto.RegisterRequest;
import com.edgesoft.chirp.dto.RegisterResponse;
import com.edgesoft.chirp.entity.User;
import com.edgesoft.chirp.service.RegistrationService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    RegistrationService registrationService;

    @GetMapping
    public ResponseEntity<User> getUser(){
        return ResponseEntity.status(HttpStatus.OK).body(new User());
    }

    // User registration
    @PostMapping("/registration")
    public ResponseEntity<RegisterResponse> registerUser(@RequestBody @Valid RegisterRequest registerRequest){
        //field validations, existing user, encryption, persistent storage
        log.info("User registration is in progress");
        return ResponseEntity.status(HttpStatus.OK).body(registrationService.register(registerRequest));

    }

}
