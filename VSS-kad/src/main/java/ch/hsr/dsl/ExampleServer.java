package ch.hsr.dsl;

import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import static ch.hsr.dsl.Protocol.*;

import java.io.IOException;
import java.util.*;

public class ExampleServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("server starting");
        ServerBuilder.forPort(5001).addService(new KadService()).build().start().awaitTermination();
    }

    private static class KadService extends KadProtocolGrpc.KadProtocolImplBase {

        @Override
        public void getPeers(Empty request, StreamObserver<PeerList> responseObserver) {
            long seed = System.currentTimeMillis();
            List<Peer> peers = createPeers(seed);
            PeerList u = PeerList.newBuilder().setSeed(seed).addAllPeers(peers).build();
            responseObserver.onNext(u);
            responseObserver.onCompleted();
        }

        @Override
        public void checkPeer(BucketRequest request, StreamObserver<Response> responseObserver) {
            Peer p = request.getPeer();
            int bucket = 32 - Integer.numberOfLeadingZeros((p.getPeerId() & 0xff) ^ (request.getSelf().getPeerId() & 0xff));
            Response c = Response.newBuilder().setSuccess(bucket == request.getBucket()).build();
            System.out.print(c.getSuccess()?".":"!");
            responseObserver.onNext(c);
            responseObserver.onCompleted();
        }

        @Override
        public void checkBucket8(PeerList request, StreamObserver<Response> responseObserver) {
            long seed = request.getSeed();
            List<Peer> peers = createPeers(seed);
            Peer self = peers.get(0);

            List<Peer> results = new ArrayList<>();
            for(Peer peer:peers) {
                int bucket = 32 - Integer.numberOfLeadingZeros((peer.getPeerId() & 0xff) ^ (self.getPeerId() & 0xff));
                if(bucket == 8 && results.size() < 20) {
                    results.add(peer);
                }
            }
            int counter = results.size();
            results.retainAll(request.getPeersList());
            System.out.println("SIZE: "+results.size());
            boolean same = request.getPeersList().size() == counter;
            Response c = Response.newBuilder().setSuccess(20 == results.size()).build();
            System.out.println(c.getSuccess() ? "SUCCESS for "+seed: "FAILURE for "+seed);
            responseObserver.onNext(c);
            responseObserver.onCompleted();
        }
    }

    private static List<Peer> createPeers(long seed) {
        Random r = new Random(seed);
        List<Peer> peers = new ArrayList<>();
        Set<Integer> ids = new HashSet<>();
        for(int i=0;i<100;i++) {
            byte[] data = new byte[6];
            r.nextBytes(data);
            int id = data[0] & 0xff;
            Peer p = Peer.newBuilder()
                    .setPeerId(id)
                    .setIpPort((data[1] & 0xff)
                            +"."+(data[2] & 0xff)
                            +"."+(data[3] & 0xff)
                            +"."+(data[4] & 0xff)
                            +":"+(data[5] & 0xff))
                    .build();
            if(ids.contains(id)) {
                i--;
            } else{
                ids.add(id);
                peers.add(p);
            }
        }
        return peers;
    }
}