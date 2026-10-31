package com.edgesoft.chirp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name="users",
        indexes = {
        @Index(name = "idx_user_email", columnList = "email", unique = true)}
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

//    userId as the primary key, username, bio, creation timestamp,
//    and optionally denormalized counts for followers and following

    @Id
    private UUID userId;

    @Column(nullable = false, length = 100)
    private String firstName;
    private String lastName;
    private String bio;
    private String location;
    private Instant createdAt;
    private Instant date_of_birth;
    private int followers = 0;
    private int following = 0;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false)
    private String password;

}
