package com.edgesoft.chirp.service;

import com.edgesoft.chirp.dto.RegisterRequest;
import com.edgesoft.chirp.dto.RegisterResponse;
import com.edgesoft.chirp.entity.User;
import com.edgesoft.chirp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RegistrationService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public RegisterResponse register(RegisterRequest registerRequest){
        /*
        1. field validations, SKIP for now
        2. existing user,
        3. encryption,
        4. persistent storage
        * */
        User user = User.builder()
                .userId(UUID.randomUUID())
                .email(registerRequest.email())
                .password(passwordEncoder.encode(registerRequest.password()))
                .firstName(registerRequest.firstName())
                        .build();
        System.out.println(user);
        userRepository.save(user);



        return new RegisterResponse(user.getFirstName(),user.getLastName(),user.getEmail());
    }
}
