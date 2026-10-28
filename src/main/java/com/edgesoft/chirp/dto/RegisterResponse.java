package com.edgesoft.chirp.dto;

import com.edgesoft.chirp.entity.User;

public record RegisterResponse(
    String firstName,
    String lastName,
    String email
) {
    public RegisterResponse(User user) {
        this(user.getFirstName(), user.getLastName(), user.getEmail());
    }
}
