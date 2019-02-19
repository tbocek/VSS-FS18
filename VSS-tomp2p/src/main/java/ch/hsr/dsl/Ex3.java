package ch.hsr.dsl;

import net.tomp2p.dht.FutureGet;
import net.tomp2p.dht.FuturePut;
import net.tomp2p.dht.PeerBuilderDHT;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.p2p.Peer;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.peers.Number160;
import net.tomp2p.storage.Data;

import java.io.IOException;

public class Ex3 {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        PeerDHT[] peers = null;
        try {
            peers = ExampleUtils.createAndAttachPeersDHT(100, 4001);
            ExampleUtils.bootstrap(peers);
            //
            String data = "This is a data item";

            peers[5].put(Number160.createHash(data)).data(new Data(data)).start().awaitUninterruptibly();

            FutureGet fg = peers[10].get(Number160.createHash(data)).start().awaitUninterruptibly();

            System.out.println("got 1: "+ fg.data().object());

            PeerDHT[] peers2 = new PeerDHT[peers.length + 5];
            for(int i=0;i<peers.length;i++) {
                peers2[i] = peers[i];
            }
            //lets insert a few peers close to the key hash(data), return the string attack
            peers2[peers.length + 0] = new PeerBuilderDHT(new PeerBuilder( Number160.createHash(data).xor(new Number160(0)) ).ports( 5000 ).start()).start();
            peers2[peers.length + 0].put(Number160.createHash(data)).data(new Data("attack")).start().awaitUninterruptibly();
            peers2[peers.length + 1] = new PeerBuilderDHT(new PeerBuilder( Number160.createHash(data).xor(new Number160(1)) ).ports( 5001 ).start()).start();
            peers2[peers.length + 1].put(Number160.createHash(data)).data(new Data("attack")).start().awaitUninterruptibly();
            peers2[peers.length + 2] = new PeerBuilderDHT(new PeerBuilder( Number160.createHash(data).xor(new Number160(2)) ).ports( 5002 ).start()).start();
            peers2[peers.length + 2].put(Number160.createHash(data)).data(new Data("attack")).start().awaitUninterruptibly();
            peers2[peers.length + 3] = new PeerBuilderDHT(new PeerBuilder( Number160.createHash(data).xor(new Number160(3)) ).ports( 5003 ).start()).start();
            peers2[peers.length + 3].put(Number160.createHash(data)).data(new Data("attack")).start().awaitUninterruptibly();
            peers2[peers.length + 4] = new PeerBuilderDHT(new PeerBuilder( Number160.createHash(data).xor(new Number160(4)) ).ports( 5004 ).start()).start();
            peers2[peers.length + 4].put(Number160.createHash(data)).data(new Data("")).start().awaitUninterruptibly();

            //add attacker to the network
            ExampleUtils.bootstrap(peers2);

            fg = peers[41].get(Number160.createHash(data)).start().awaitUninterruptibly();
            System.out.println("got 2: "+ fg.data().object());

        } finally {
            // 0 is the master
            if (peers != null && peers[0] != null) {
                peers[0].shutdown();
            }
        }
    }
}
