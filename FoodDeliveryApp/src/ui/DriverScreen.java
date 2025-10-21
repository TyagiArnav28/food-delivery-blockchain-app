package ui;

import core.Navigator;
import core.CardLayoutPanel; 
import core.Block;           
import core.OrderStatusEvent;  
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList; 
import java.util.HashSet;   

public class DriverScreen extends JPanel {

    private Navigator navigator;
    private JTextArea orderDisplay;
    private CardLayoutPanel controller; 
    
    private ArrayList<OrderStatusEvent> ordersToPickUp;
    private ArrayList<OrderStatusEvent> ordersToDeliver;

 // --- UPDATED CONSTRUCTOR ---
    public DriverScreen(Navigator navigator, CardLayoutPanel controller) {
        this.navigator = navigator;
        this.controller = controller; 
        this.ordersToPickUp = new ArrayList<>();
        this.ordersToDeliver = new ArrayList<>();
        
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(34, 34, 34));

        // Title (No Emoji)
        JLabel title = new JLabel("Driver Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        add(title, BorderLayout.NORTH);

        // Display area for orders
        orderDisplay = new JTextArea("Click 'Refresh Pickups' to find new orders...");
        orderDisplay.setEditable(false);
        orderDisplay.setFont(new Font("Monospaced", Font.PLAIN, 14));
        orderDisplay.setBackground(new Color(50, 50, 50));
        orderDisplay.setForeground(Color.WHITE);
        add(new JScrollPane(orderDisplay), BorderLayout.CENTER);

        // --- BUTTON PANEL ---
        // **THE FIX: Use GridLayout to force all 4 buttons to show**
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 15, 10)); // 1 row, 4 columns, 15px h-gap, 10px v-gap
        buttonPanel.setBackground(new Color(34, 34, 34));

        JButton homeButton = new JButton("Back to Home");
        styleButton(homeButton, new Color(90, 90, 90));

        JButton refreshButton = new JButton("Refresh Pickups");
        styleButton(refreshButton, new Color(0, 100, 200));

        JButton pickupButton = new JButton("Pick Up First Order");
        styleButton(pickupButton, new Color(0, 150, 136));
        
        JButton deliverButton = new JButton("Deliver First Order");
        styleButton(deliverButton, new Color(200, 100, 0)); 
        
        // --- ADD LOGIC TO BUTTONS ---
        homeButton.addActionListener(e -> navigator.showScreen("home"));
        refreshButton.addActionListener(e -> findAvailableOrders());
        pickupButton.addActionListener(e -> pickUpFirstOrder());
        deliverButton.addActionListener(e -> deliverFirstOrder());

        // --- ADD ALL BUTTONS TO PANEL ---
        buttonPanel.add(homeButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(pickupButton);
        buttonPanel.add(deliverButton);
        
        // --- DEBUG PRINT ---
        // This will print "4" to your console if all buttons were added
        System.out.println("Buttons in driver panel: " + buttonPanel.getComponentCount());
        
        add(buttonPanel, BorderLayout.SOUTH);
    }

    // --- This is the new, smart refresh method ---
    private void findAvailableOrders() {
        ordersToPickUp.clear();
        ordersToDeliver.clear();
        StringBuilder sb = new StringBuilder();

        // 1. Find all Order IDs that are already "Picked Up"
        HashSet<String> pickedUpIds = new HashSet<>();
        for (Block block : controller.blockchain) {
            for (OrderStatusEvent event : block.transactions) {
                if (event.statusDetails.startsWith("Order Picked Up")) {
                    pickedUpIds.add(event.orderId);
                }
            }
        }
        
        // 2. Find all Order IDs that are already "Delivered"
        HashSet<String> deliveredIds = new HashSet<>();
        for (Block block : controller.blockchain) {
            for (OrderStatusEvent event : block.transactions) {
                if (event.statusDetails.startsWith("Order Delivered")) {
                    deliveredIds.add(event.orderId);
                }
            }
        }

        // 3. Scan the blockchain
        sb.append("--- ORDERS TO PICK UP ---\n");
        for (Block block : controller.blockchain) {
            for (OrderStatusEvent event : block.transactions) {
                
                // Find "Confirmed" orders that are NOT yet picked up AND NOT yet delivered
                if (event.statusDetails.startsWith("Order Confirmed") 
                        && !pickedUpIds.contains(event.orderId) 
                        && !deliveredIds.contains(event.orderId)) {
                            
                    ordersToPickUp.add(event);
                    sb.append("Order ID: ").append(event.orderId).append("\n\n");
                }
                
                // Find "Picked Up" orders that are NOT yet delivered
                if (event.statusDetails.startsWith("Order Picked Up") 
                        && !deliveredIds.contains(event.orderId)) {
                            
                    boolean alreadyInList = false;
                    for(OrderStatusEvent deliverEvent : ordersToDeliver) {
                        if (deliverEvent.orderId.equals(event.orderId)) {
                            alreadyInList = true;
                            break;
                        }
                    }
                    if (!alreadyInList) {
                        ordersToDeliver.add(event);
                    }
                }
            }
        }
        
        // 4. List the orders to deliver
        sb.append("--- ORDERS TO DELIVER ---\n");
        if (ordersToDeliver.isEmpty()) {
            sb.append("No orders to deliver.\n");
        } else {
            for (OrderStatusEvent event : ordersToDeliver) {
                sb.append("Order ID: ").append(event.orderId).append("\n\n");
            }
        }
        
        if (ordersToPickUp.isEmpty() && ordersToDeliver.isEmpty()) {
            orderDisplay.setText("No available orders found.");
        } else {
            orderDisplay.setText(sb.toString());
        }
    }

    // --- Picks up the first order in the list ---
    private void pickUpFirstOrder() {
        if (ordersToPickUp.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No orders to pick up. Please refresh.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        OrderStatusEvent orderToPickUp = ordersToPickUp.get(0);

        System.out.println("\nEvent 3: Driver is picking up order: " + orderToPickUp.orderId);
        
        OrderStatusEvent event3 = new OrderStatusEvent(
            CardLayoutPanel.driverWallet.publicKey, // Use static access
            orderToPickUp.orderId,
            "Order Picked Up by Driver"
        );

        event3.generateSignature(CardLayoutPanel.driverWallet.privateKey);

        Block newBlock = new Block(CardLayoutPanel.blockchain.get(CardLayoutPanel.blockchain.size() - 1).hash);
        newBlock.addTransaction(event3);

        CardLayoutPanel.addBlock(newBlock);

        JOptionPane.showMessageDialog(this, "Order Picked Up!\nBlock added to chain!", "Pickup Success", JOptionPane.INFORMATION_MESSAGE);
        findAvailableOrders(); // Refresh the list
        System.out.println("\nBlockchain is Valid: " + CardLayoutPanel.isChainValid());
    }
    
    // --- Delivers the first order in the list ---
    private void deliverFirstOrder() {
        if (ordersToDeliver.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No orders to deliver. Please refresh.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        OrderStatusEvent orderToDeliver = ordersToDeliver.get(0);

        System.out.println("\nEvent 4: Driver is delivering order: " + orderToDeliver.orderId);
        
        OrderStatusEvent event4 = new OrderStatusEvent(
            CardLayoutPanel.driverWallet.publicKey, // Use static access
            orderToDeliver.orderId,
            "Order Delivered"
        );

        event4.generateSignature(CardLayoutPanel.driverWallet.privateKey);

        Block newBlock = new Block(CardLayoutPanel.blockchain.get(CardLayoutPanel.blockchain.size() - 1).hash);
        newBlock.addTransaction(event4);

        CardLayoutPanel.addBlock(newBlock);

        JOptionPane.showMessageDialog(this, "Order Delivered!\nBlock added to chain!", "Delivery Success", JOptionPane.INFORMATION_MESSAGE);
        findAvailableOrders(); // Refresh the list
        System.out.println("\nBlockchain is Valid: " + CardLayoutPanel.isChainValid());
    }

    // --- This is the new styleButton method ---
    private void styleButton(JButton btn, Color color) {
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
    }
}