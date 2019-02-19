package ch.hsr.dsl;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import static ch.hsr.dsl.Protocol.*;

public class RegistrationClient {

    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 5001).usePlaintext().build();
        RegistrationProtocolGrpc.RegistrationProtocolBlockingStub blockingStub = RegistrationProtocolGrpc.newBlockingStub(channel);

        RequestMessage request = RequestMessage.newBuilder()
                .setFirstName("Thomas")
                .setLastName("Bocek")
                .setAffiliation("HSR")
                .setPin(1337)
                .build();
        System.out.println("client sends: "+request.getPin());
        ReplyMessage reply = blockingStub.register(request);
        System.out.println("client received: "+reply.getReply());
    }
}
