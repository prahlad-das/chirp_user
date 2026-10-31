package com.edgesoft.chirp.utils;

import com.edgesoft.chirp.dto.UserResponse;
import com.edgesoft.chirp.proto.UserReply;
import com.google.protobuf.Timestamp;

import java.time.Instant;

public class UserConvert {

    public static UserReply convert(UserResponse userResponse) {
        if (userResponse == null) {
            return UserReply.getDefaultInstance();
        }

        return UserReply.newBuilder()
                .setUserId(orEmpty(userResponse.getUserId()))
                .setEmail(orEmpty(userResponse.getEmail()))
                .setFirstName(orEmpty(userResponse.getFirstName()))
                .setLastName(orEmpty(userResponse.getLastName()))
                .setBio(orEmpty(userResponse.getBio()))
                .setLocation(orEmpty(userResponse.getLocation()))
                .setFollowers(userResponse.getFollowers())
                .setFollowing(userResponse.getFollowing())
                .setCreatedAt(toTimestamp(userResponse.getCreatedAt()))
                .setDob(toTimestamp(userResponse.getDate_of_birth()))
                .build();
    }

    private static String orEmpty(Object value) {
        return value != null ? value.toString() : "";
    }

    private static Timestamp toTimestamp(Instant date) {
        if (date == null) {
            return Timestamp.getDefaultInstance();
        }
        return Timestamp.newBuilder()
                .setSeconds(date.getEpochSecond())
                .setNanos(date.getNano())
                .build();
    }
}

