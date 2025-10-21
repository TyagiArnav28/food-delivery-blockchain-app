package core; // Make sure package is 'core'

import java.util.ArrayList;
import java.util.Date;

public class Block {

    public String hash;
    public String previousHash;
    public String merkleRoot;
    
    // **THIS IS THE FIX**
    public ArrayList<OrderStatusEvent> transactions = new ArrayList<OrderStatusEvent>();
    
    private long timeStamp;
    private int nonce;

    //Block Constructor.
    public Block(String previousHash) {
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash(); 
    }

    //Calculate new hash based on blocks contents
    public String calculateHash() {
        String calculatedhash = StringUtil.applySha256(
                previousHash +
                Long.toString(timeStamp) +
                Integer.toString(nonce) +
                merkleRoot
                );
        return calculatedhash;
    }

    //Increases nonce value until hash target is reached.
    public void mineBlock(int difficulty) {
        merkleRoot = StringUtil.getMerkleRoot(transactions); // This will now work
        String target = StringUtil.getDificultyString(difficulty);
        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = calculateHash();
        }
        System.out.println("Block Mined!!! : " + hash);
    }

    //Add transactions to this block
    // **THIS IS THE OTHER FIX**
    public boolean addTransaction(OrderStatusEvent transaction) {
        if (transaction == null) return false;
        
        // In a real app, we'd do more checks here
        
        transactions.add(transaction);
        System.out.println("Transaction Successfully added to Block");
        return true;
    }
}