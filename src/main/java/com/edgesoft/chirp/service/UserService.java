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

import java.time.Instant;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.UUID;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Cacheable(value = "users-email", key = "#email")
    public @Nullable UserResponse getUserByEmail(String email) {
        String sanitizedEmail = email.strip().toLowerCase(Locale.ROOT);
        log.info("Getting the user by email: {}", sanitizedEmail);
        return userRepository.findByEmail(sanitizedEmail)
                .map(user -> {
                    log.info("Found the user: {}", user.getFirstName());
                    return new UserResponse(user);
                }).orElseThrow(
                        () -> new NoSuchElementException("User not found with email: {}" + sanitizedEmail));
    }

    public RegisterResponse register(RegisterRequest registerRequest) {
        /*
         * 1. field validations, SKIP for now
         * 2. existing user, Throw Exception
         * 3. encryption, BCrypt
         * 4. persistent storage, Postgres
         */
        User user = User.builder()
                .userId(UUID.randomUUID())
                .email(registerRequest.email())
                .password(passwordEncoder.encode(registerRequest.password()))
                .firstName(registerRequest.firstName())
                .createdAt(Instant.now())
                .build();

        User savedUser = userRepository.saveAndFlush(user);
        log.info("Registered a new user {}", savedUser.getFirstName());
        return new RegisterResponse(savedUser);

    }

    @Cacheable(value = "users", key = "#id")
    public @Nullable UserResponse getUserById(UUID id) {
        log.info("Finding a user by id: {}", id);
        return userRepository.findById(id).map(
                user -> {
                    log.info("User found with uuid: {}", id);
                    return new UserResponse(user);
                }).orElseThrow(
                        () -> new NoSuchElementException("No user found with id " + id));
    }

}
