package ch.hsr.dsl;

import org.apache.avro.ipc.NettyTransceiver;
import org.apache.avro.ipc.specific.SpecificRequestor;

import java.io.IOException;
import java.net.InetSocketAddress;

public class ExampleClient {
    public static void main(String[] args) throws IOException {
        NettyTransceiver client = new NettyTransceiver(new InetSocketAddress(7001));
        MyProtocol proxy = SpecificRequestor.getClient(MyProtocol.class, client);
        AMessage aMessage = AMessage.newBuilder().setRequest("world").setCode(0).build();
        System.out.println("client sends: "+aMessage.getRequest());
        BMessage bMessage = proxy.GetMessage(aMessage);
        System.out.println("client received: "+bMessage.getReply());
        client.close();
    }
}