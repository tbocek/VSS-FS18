package ch.hsr.dsl;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import static ch.hsr.dsl.Protocol.*;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExampleClient {

    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 5001).usePlaintext().build();
        KadProtocolGrpc.KadProtocolBlockingStub blockingStub = KadProtocolGrpc.newBlockingStub(channel);
        PeerList unsorted = blockingStub.getPeers(Empty.newBuilder().build());
        Peer self = unsorted.getPeers(0);
        long seed = unsorted.getSeed();
        Map<Integer,List<Peer>> resultsMap = new HashMap<>();
        List<Peer> results = new ArrayList<>();
        for(int i=0;i<unsorted.getPeersCount();i++) {
            Protocol.Peer peer = unsorted.getPeers(i);

            int bucket = 32 - Integer.numberOfLeadingZeros(self.getPeerId() ^ peer.getPeerId());
            BucketRequest b = BucketRequest.newBuilder()
                    .setSelf(self)
                    .setPeer(peer)
                    .setBucket(bucket).build();
            Response r = blockingStub.checkPeer(b);

            System.out.println(
                    (r.getSuccess() ? "SUCCESS":"FAILURE")+
                            " for peerId: "+peer.getPeerId()+
                            " I'm "+self.getPeerId()+
                            " bucket="+bucket+
                            " IP="+peer.getIpPort());

            //check if results stored in map, if yes reuse
            //otherwise put new list into map
            //resultsMap.put(bucket, results);
            if(bucket == 8 && results.size() < 20) {
                results.add(peer);
            }

        }

        Response r = blockingStub.checkBucket8(PeerList.newBuilder().setSeed(seed).addAllPeers(results).build());
        System.out.println("Check bucket: ***************************************************************************************************************************************");
        System.out.println(r.getSuccess() ? "SUCCESS":"FAILURE");
        for(Peer peer:results) {
            System.out.println(
                            " for peerId: "+peer.getPeerId()+
                            " I'm "+self.getPeerId()+
                            " bucket=8, IP="+peer.getIpPort());
        }
    }
}
