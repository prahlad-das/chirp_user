package com.edgesoft.chirp.service;

import com.edgesoft.chirp.dto.RegisterRequest;
import com.edgesoft.chirp.dto.RegisterResponse;
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
    public User getUserByEmail(String email) {
        log.info("Getting the user by email: " + email);
        Optional<User> user = userRepository.findByEmail(email);
        log.info("found the user " + user.get().getFirstName());
        return user.get();
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
    public @Nullable User getUserById(UUID id) {
        log.info("Finding a user by id: " + id);
        Optional<User> user = userRepository.findById(id);
        log.info("Found a user by id " + user.get().getFirstName());
        return user.get();
    }
}
