package com.edgesoft.chirp.dto;

public record RegisterResponse(
    String firstName,
    String lastName,
    String email
) {
}
