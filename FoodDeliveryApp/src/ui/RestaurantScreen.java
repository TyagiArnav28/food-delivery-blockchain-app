package ui;

import java.util.HashSet; // <-- ADD THIS
import core.Navigator;
import core.CardLayoutPanel; // Import our controller
import core.Block;           // Import Block
import core.OrderStatusEvent;  // Import our Event class
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList; // Import ArrayList

public class RestaurantScreen extends JPanel {

    private Navigator navigator;
    private JTextArea orderDisplay;
    private CardLayoutPanel controller; // <-- NEW: A reference to our controller
    private ArrayList<OrderStatusEvent> pendingOrders; // <-- NEW: To store found orders

    // --- UPDATED CONSTRUCTOR ---
    public RestaurantScreen(Navigator navigator, CardLayoutPanel controller) {
        this.navigator = navigator;
        this.controller = controller; // <-- NEW
        this.pendingOrders = new ArrayList<>(); // <-- NEW
        
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(34, 34, 34));

        // Title
        JLabel title = new JLabel("Restaurant Dashboard 🍳", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        add(title, BorderLayout.NORTH);

        // Display area for orders
        orderDisplay = new JTextArea("Click 'Refresh Orders' to find new orders...");
        orderDisplay.setEditable(false);
        // ... (rest of display setup)
        orderDisplay.setFont(new Font("Monospaced", Font.PLAIN, 14));
        orderDisplay.setBackground(new Color(50, 50, 50));
        orderDisplay.setForeground(Color.WHITE);
        add(new JScrollPane(orderDisplay), BorderLayout.CENTER);

     // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10)); // Added spacing
        buttonPanel.setBackground(new Color(34, 34, 34));

        // --- NEW BACK TO HOME BUTTON ---
        JButton homeButton = new JButton("⬅ Back to Home");
        styleButton(homeButton, new Color(90, 90, 90)); // A neutral color
        homeButton.addActionListener(e -> navigator.showScreen("home"));
        
        JButton confirmButton = new JButton("Confirm First Order");
        styleButton(confirmButton, new Color(0, 150, 136));
        
        JButton refreshButton = new JButton("Refresh Orders 🔄");
        styleButton(refreshButton, new Color(0, 100, 200));

        // --- ADD LOGIC TO BUTTONS ---
        refreshButton.addActionListener(e -> findPendingOrders());
        confirmButton.addActionListener(e -> confirmFirstOrder());

        // --- ADD ALL BUTTONS TO PANEL ---
        buttonPanel.add(homeButton);
        buttonPanel.add(confirmButton);
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

 // --- NEW METHOD: Finds all "Order Placed" events ---
    private void findPendingOrders() {
        pendingOrders.clear();
        StringBuilder sb = new StringBuilder();
        sb.append("--- PENDING ORDERS ---\n");

        // --- NEW LOGIC ---
        // 1. First, find all Order IDs that have already been confirmed.
        HashSet<String> confirmedOrderIds = new HashSet<>();
        for (Block block : controller.blockchain) {
            for (OrderStatusEvent event : block.transactions) {
                if (event.statusDetails.startsWith("Order Confirmed")) {
                    confirmedOrderIds.add(event.orderId);
                }
            }
        }

        // 2. Scan the blockchain again for "Order Placed" events
        for (Block block : controller.blockchain) {
            for (OrderStatusEvent event : block.transactions) {
                
                // We're looking for the *first* event for an order
                if (event.statusDetails.startsWith("Order Placed")) {
                    
                    // 3. Check if this order ID is in our set of confirmed orders.
                    // If it's NOT confirmed, then it's a pending order.
                    if (!confirmedOrderIds.contains(event.orderId)) {
                        pendingOrders.add(event);
                        sb.append("Order ID: ").append(event.orderId).append("\n");
                        sb.append("Details: ").append(event.statusDetails).append("\n\n");
                    }
                }
            }
        }
        
        if (pendingOrders.isEmpty()) {
            orderDisplay.setText("No pending orders found.");
        } else {
            orderDisplay.setText(sb.toString());
        }
    }
    // --- NEW METHOD: Confirms the first order in the list ---
    private void confirmFirstOrder() {
        if (pendingOrders.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No orders to confirm. Please refresh.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get the first pending order
        OrderStatusEvent orderToConfirm = pendingOrders.get(0);

        // 1. Create Event 2: "Order Confirmed"
        System.out.println("\nEvent 2: Restaurant is confirming order: " + orderToConfirm.orderId);
        
        OrderStatusEvent event2 = new OrderStatusEvent(
            controller.restaurantWallet.publicKey, // Signed by the RESTAURANT
            orderToConfirm.orderId,
            "Order Confirmed by Restaurant"
        );

        // 2. Sign the Event with the Restaurant's private key
        event2.generateSignature(controller.restaurantWallet.privateKey);

        // 3. Create a new Block
        Block newBlock = new Block(controller.blockchain.get(controller.blockchain.size() - 1).hash);
        
        // 4. Add the Event to the block
        newBlock.addTransaction(event2);

        // 5. Mine and Add the Block to the chain
        controller.addBlock(newBlock);

        // 6. Show success and refresh the list
        JOptionPane.showMessageDialog(this,
            "Order Confirmed!\nOrder ID: " + orderToConfirm.orderId + "\nBlock added to chain!",
            "Order Confirmed",
            JOptionPane.INFORMATION_MESSAGE);
            
        findPendingOrders(); // Refresh the list
        
        // 7. Verify the chain
        System.out.println("\nBlockchain is Valid: " + controller.isChainValid());
    }

    private void styleButton(JButton btn, Color color) {
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
    }
}