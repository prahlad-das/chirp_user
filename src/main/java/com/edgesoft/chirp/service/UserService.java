package com.edgesoft.chirp.service;

import com.edgesoft.chirp.dto.RegisterRequest;
import com.edgesoft.chirp.dto.RegisterResponse;
import com.edgesoft.chirp.dto.UserResponse;
import com.edgesoft.chirp.entity.User;
import com.edgesoft.chirp.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Cacheable(value="users-email", key="#email")
    public @Nullable UserResponse getUserByEmail(String email) {
        log.info("Getting the user by email: " + email);
        Optional<UserResponse> userResponse = userRepository.findByEmail(email).map(UserResponse::new);
        log.info("found the user " + userResponse.get().getFirstName());
        return userResponse.get();
    }

    public RegisterResponse register(RegisterRequest registerRequest) {
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

        User savedUser = userRepository.saveAndFlush(user);
        log.info("Registered a new user " + savedUser.getFirstName());
        return new RegisterResponse(savedUser.getFirstName(), savedUser.getLastName(), savedUser.getEmail());


    }

    @Cacheable(value="users", key="#id")
    public @Nullable UserResponse getUserById(UUID id) {
        log.info("Finding a user by id: " + id);
        Optional<UserResponse> userResponse = userRepository.findById(id).map(UserResponse::new);
        log.info("Found a user by id " + userResponse.get().getFirstName());
        return userResponse.get();
    }
}
