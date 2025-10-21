package ui;

import core.Navigator;
import core.CartItem;
import core.CardLayoutPanel; // Import our controller
import core.Block;           // Import Block
import core.OrderStatusEvent;  // Import our Event class
import core.Wallet;          // Import Wallet
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.UUID; // To generate unique order IDs

public class CartScreen extends JPanel {
    private Navigator navigator;
    private List<CartItem> cartItems;
    private JTextArea cartDisplay;
    private JLabel totalLabel;
    
    // --- NEW ---
    private CardLayoutPanel controller; 

    // **UPDATED CONSTRUCTOR**
    public CartScreen(Navigator navigator, List<CartItem> cartItems, CardLayoutPanel controller) { // Added controller
        this.navigator = navigator;
        this.cartItems = cartItems;
        this.controller = controller; // --- NEW ---

        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(34, 34, 34));

        JLabel title = new JLabel("🛒 Your Cart", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        add(title, BorderLayout.NORTH);

        cartDisplay = new JTextArea();
        cartDisplay.setEditable(false);
        cartDisplay.setFont(new Font("Monospaced", Font.PLAIN, 14));
        cartDisplay.setBackground(new Color(50, 50, 50));
        cartDisplay.setForeground(Color.WHITE);
        add(new JScrollPane(cartDisplay), BorderLayout.CENTER);

        // --- NEW BUTTON PANEL ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(34, 34, 34));

        totalLabel = new JLabel("Total: $0", SwingConstants.RIGHT);
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        totalLabel.setForeground(new Color(0, 150, 136));
        bottomPanel.add(totalLabel, BorderLayout.NORTH);

        JButton backBtn = new JButton("⬅ Back to Menu");
        styleButton(backBtn);
        backBtn.addActionListener(e -> navigator.showScreen("menu"));
        
        JButton placeOrderBtn = new JButton("Place Order 🚀");
        styleButton(placeOrderBtn);
        placeOrderBtn.setBackground(new Color(0, 100, 200)); 
        
        placeOrderBtn.addActionListener(e -> {
            placeOrder(); 
        });

        JPanel buttonRow = new JPanel(new FlowLayout());
        buttonRow.setBackground(new Color(34, 34, 34));
        buttonRow.add(backBtn);
        buttonRow.add(placeOrderBtn);
        
        bottomPanel.add(buttonRow, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    // --- THIS IS OUR NEW "PLACE ORDER" METHOD ---
    private void placeOrder() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your cart is empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String orderId = UUID.randomUUID().toString();
        
        System.out.println("\nEvent 1: Customer is placing order: " + orderId);
        
        StringBuilder orderDetails = new StringBuilder("Order Placed: ");
        for (CartItem item : cartItems) {
            orderDetails.append(item.quantity).append("x ").append(item.name).append(", ");
        }
        
        // Use static access from CardLayoutPanel
        OrderStatusEvent event1 = new OrderStatusEvent(
            CardLayoutPanel.customerWallet.publicKey, 
            orderId, 
            orderDetails.toString()
        );

        event1.generateSignature(CardLayoutPanel.customerWallet.privateKey);
        
        Block newBlock = new Block(CardLayoutPanel.blockchain.get(CardLayoutPanel.blockchain.size() - 1).hash);
        
        newBlock.addTransaction(event1);
        
        CardLayoutPanel.addBlock(newBlock);
        
        JOptionPane.showMessageDialog(this, 
            "Order Placed Successfully!\nOrder ID: " + orderId + "\nBlock added to chain!", 
            "Order Success", 
            JOptionPane.INFORMATION_MESSAGE);
            
        cartItems.clear();
        refresh(cartItems);
        navigator.showScreen("home");
        
        System.out.println("\nBlockchain is Valid: " + CardLayoutPanel.isChainValid());
    }

    public void refresh(List<CartItem> updatedItems) {
        this.cartItems = updatedItems;
        cartDisplay.setText("");
        double total = 0;
        StringBuilder sb = new StringBuilder();
        for (CartItem item : cartItems) {
            sb.append(item.quantity)
              .append(" x ")
              .append(item.name)
              .append(": $")
              .append(item.getTotalPrice())
              .append("\n");
            total += item.getTotalPrice();
        }
        cartDisplay.setText(sb.toString());
        totalLabel.setText(String.format("Total: $%.2f", total));
    }

    private void styleButton(JButton btn) {
        btn.setBackground(new Color(0, 150, 136));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
    }
}