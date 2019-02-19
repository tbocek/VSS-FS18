package ch.hsr.dsl;

import org.apache.avro.AvroRemoteException;
import org.apache.avro.ipc.NettyServer;
import org.apache.avro.ipc.specific.SpecificResponder;

import java.net.InetSocketAddress;

public class ExampleServer {
    public static void main(String[] args) {
        System.out.println("server starting");
        new NettyServer(new SpecificResponder(MyProtocol.class, new MyService()), new InetSocketAddress(7001));
    }

    private static class MyService implements MyProtocol {
        @Override
        public BMessage GetMessage(AMessage request) {
            System.out.println("server received: " + request.getRequest()+ ","+request.getCode());
            BMessage bMessage = BMessage.newBuilder().setReply("hallo").build();
            System.out.println("server replied: " + bMessage.getReply());
            return bMessage;
        }
    }
}