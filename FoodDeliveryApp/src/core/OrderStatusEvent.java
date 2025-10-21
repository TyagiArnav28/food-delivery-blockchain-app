package core; // This should already be correct

import java.security.*;

public class OrderStatusEvent {

    public String eventId; // The hash of the event
    public PublicKey participant; // The public key of the participant (Customer, Restaurant, etc.)
    public byte[] signature; // The digital signature

    public String orderId;
    public String statusDetails; // e.g., "Order Placed", "Order Confirmed"

    private static int sequence = 0;

    // Constructor
    public OrderStatusEvent(PublicKey participant, String orderId, String statusDetails) {
        this.participant = participant;
        this.orderId = orderId;
        this.statusDetails = statusDetails;
        this.eventId = calculateHash();
    }

    // Calculate the event hash
    private String calculateHash() {
        sequence++; // Ensure each hash is unique
        return StringUtil.applySha256(
                StringUtil.getStringFromKey(participant) +
                orderId +
                statusDetails +
                sequence
        );
    }

    // Sign the event data
    public void generateSignature(PrivateKey privateKey) {
        String data = StringUtil.getStringFromKey(participant) + orderId + statusDetails;
        signature = StringUtil.applyECDSASig(privateKey, data);
    }

    // Verify the signature
    public boolean verifySignature() {
        String data = StringUtil.getStringFromKey(participant) + orderId + statusDetails;
        return StringUtil.verifyECDSASig(participant, data, signature);
    }
}
