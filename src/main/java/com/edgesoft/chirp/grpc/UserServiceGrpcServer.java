package com.edgesoft.chirp.grpc;

import com.edgesoft.chirp.dto.UserResponse;
import com.edgesoft.chirp.proto.UserEmail;
import com.edgesoft.chirp.proto.UserId;
import com.edgesoft.chirp.proto.UserReply;
import com.edgesoft.chirp.proto.UserServiceGrpc;
import com.edgesoft.chirp.service.UserService;
import com.edgesoft.chirp.utils.UserConvert;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.server.service.GrpcService;

import java.util.UUID;

@GrpcService
public class UserServiceGrpcServer extends UserServiceGrpc.UserServiceImplBase {

    @Autowired
    private UserService userService;

    @Override
    public void getUserByEmail(UserEmail email, StreamObserver<UserReply> responseObserver) {
        UserResponse user = userService.getUserByEmail(email.getEmail());
        UserReply userReply = UserConvert.convert(user);
        responseObserver.onNext(userReply);
        responseObserver.onCompleted();
    }

    @Override
    public void getUserById(UserId userId, StreamObserver<UserReply> responseObserver) {
        UUID userUuid = UUID.fromString(userId.getUuid());
        UserResponse user = userService.getUserById(userUuid);
        UserReply userReply = UserConvert.convert(user);
        responseObserver.onNext(userReply);
        responseObserver.onCompleted();
    }


}
