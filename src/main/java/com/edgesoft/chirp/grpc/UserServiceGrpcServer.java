package com.edgesoft.chirp.grpc;

import com.edgesoft.chirp.dto.UserResponse;
import com.edgesoft.chirp.proto.UserReply;
import com.edgesoft.chirp.proto.UserRequest;
import com.edgesoft.chirp.proto.UserServiceGrpc;
import com.edgesoft.chirp.service.UserService;
import com.edgesoft.chirp.utils.UserConvert;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
public class UserServiceGrpcServer extends UserServiceGrpc.UserServiceImplBase {

    @Autowired
    private UserService userService;

    @Override
    public void getUser(UserRequest userRequest, StreamObserver<UserReply> responseObserver) {
        UserResponse user = userService.getUser(userRequest.getId());
        UserReply userReply = UserConvert.convert(user);
        responseObserver.onNext(userReply);
        responseObserver.onCompleted();
    }
}
