package ch.hsr.dsl;

import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import static ch.hsr.dsl.Protocol.*;

import java.io.IOException;

public class RegistrationServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("server starting");
        ServerBuilder.forPort(5001).addService(new MyService()).build().start().awaitTermination();
    }

    private static class MyService extends RegistrationProtocolGrpc.RegistrationProtocolImplBase {
        @Override
        public void register(RequestMessage request, StreamObserver<ReplyMessage> responseObserver) {
            final ReplyMessage reply;
            if(request.getPin() == 1337) {
                reply = ReplyMessage.newBuilder().setReply(
                        "SUCCESS, welcome "
                                +request.getFirstName()
                                + " "
                                + request.getLastName()
                                +" from "
                                +request.getAffiliation()).build();
            } else {
                reply = ReplyMessage.newBuilder().setReply(
                        "FAILED: wrong pin: "
                                +request.getPin()
                                +" for "
                                +request.getFirstName()
                                + " "
                                + request.getLastName()).build();
            }
            responseObserver.onNext(reply);
            System.out.println("server replied: " + reply.getReply());
            responseObserver.onCompleted();
        }
    }
}