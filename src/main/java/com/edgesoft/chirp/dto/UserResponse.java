package com.edgesoft.chirp.dto;

import com.edgesoft.chirp.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse implements Serializable {

    public UserResponse(User user) {
        this.userId = user.getUserId();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.bio = user.getBio();
        this.date_of_birth = user.getDate_of_birth();
        this.location = user.getLocation();
        this.followers = user.getFollowers();
        this.following = user.getFollowing();
        this.createdAt = user.getCreatedAt();
    }

    private UUID userId;

    private String firstName;
    private String lastName;
    private String bio;
    private String location;
    private Date createdAt;
    private Date date_of_birth;
    private int followers = 0;
    private int following = 0;

    private String email;

}
