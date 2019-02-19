package ch.hsr.dsl.bitcoin;

import org.bitcoinj.core.*;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.UnitTestParams;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.store.MemoryBlockStore;
import org.bitcoinj.testing.*;

import java.util.List;

import static com.google.common.base.Preconditions.checkState;

public class Tx {

    private static final NetworkParameters UNITTEST = UnitTestParams.get();
    public static void main(String[] args) throws BlockStoreException {

        final BlockStore blockStore = new MemoryBlockStore(UNITTEST);
        Context.getOrCreate(UNITTEST);

        //PE2
        ECKey alice = new ECKey();
        ECKey bob = new ECKey();
        ECKey carol = new ECKey();
        ECKey dave = new ECKey();
        ECKey eve = new ECKey();
        System.out.println("Key of Alice: "+alice);

        //PE3
        Transaction tx = new Transaction(UNITTEST);
        tx.addOutput(Coin.valueOf(5000000000L), alice);
        System.out.println("Invalid TX to Alice: "+tx);
        Block b = FakeTxBuilder.createFakeBlock(blockStore, tx).block;
        System.out.println("Block: "+b);
        //check(tx);

        //PE4
        Transaction tx2 = new Transaction(UNITTEST);
        tx2.addOutput(Coin.valueOf(49,0), bob);
        tx2.addOutput(Coin.valueOf(1, 0), carol);
        tx2.addSignedInput(tx.getOutput(0), alice);
        check(tx2);
        System.out.println("Valid TX: "+tx2);

        //PE5
        Block b2 = FakeTxBuilder.makeSolvedTestBlock(
                b, LegacyAddress.fromKey(UNITTEST, dave), tx2);
        System.out.println("Block 2: "+b2);

        //PE6
        Transaction tx3 = new Transaction(UNITTEST);
        tx3.addOutput(Coin.valueOf(25, 0), eve);
        tx3.addSignedInput(b2.getTransactions().get(1).getOutput(0), dave);
        check(tx3);
        System.out.println("TX 3: "+tx3);

        System.out.println("done.");

    }

    private static void check(Transaction tx) {
        tx.verify();
        Coin amount = Coin.ZERO;
        List<TransactionInput> inputs = tx.getInputs();
        for(int i = 0; i < inputs.size(); i++) {
            TransactionInput input = inputs.get(i);
            System.out.println("aaainput: "+input);
            Script scriptSig = input.getScriptSig();
            Script scriptPubKey = input.getConnectedOutput().getScriptPubKey();
            amount = amount.add(input.getConnectedOutput().getValue());
            scriptSig.correctlySpends(tx, i, scriptPubKey, Script.ALL_VERIFY_FLAGS);
        }

        if(tx.getOutputSum().isGreaterThan(amount)) {
            System.out.println("output: "+tx.getOutputSum());
            System.out.println("input: "+amount);
            throw new RuntimeException("cannot spend more than we have");
        }
        System.out.println("output: "+tx.getOutputSum());
        System.out.println("input: "+amount);
    }
}
