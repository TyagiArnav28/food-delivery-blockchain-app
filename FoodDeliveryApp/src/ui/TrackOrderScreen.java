package ui;

import core.Navigator;
import core.CardLayoutPanel; 
import core.Block;           
import core.OrderStatusEvent;  
import javax.swing.*;
import java.awt.*;
import java.awt.Font;          // <-- Make sure this is imported
import javax.swing.JScrollPane; // <-- Make sure this is imported

public class TrackOrderScreen extends JPanel {

    private Navigator navigator;
    private JTextArea resultDisplay;
    private JTextField orderIdField;
    private CardLayoutPanel controller; 

    // --- CONSTRUCTOR ---
    public TrackOrderScreen(Navigator navigator, CardLayoutPanel controller) {
        this.navigator = navigator;
        this.controller = controller; 
        
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(34, 34, 34));

        // Title
        JLabel title = new JLabel("Track Your Order", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        add(title, BorderLayout.NORTH);

        // Input Panel (Top-Center)
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        inputPanel.setBackground(new Color(34, 34, 34));
        
        JLabel idLabel = new JLabel("Enter Order ID:");
        idLabel.setForeground(Color.WHITE);
        orderIdField = new JTextField(20);
        
        JButton trackButton = new JButton("Track Order");
        // This is the correct call
        styleButton(trackButton, new Color(0, 100, 200)); 
        
        // --- ADD LOGIC TO THE TRACK BUTTON ---
        trackButton.addActionListener(e -> trackOrder());
        
        inputPanel.add(idLabel);
        inputPanel.add(orderIdField);
        inputPanel.add(trackButton);

        // Result Display Area (Main-Center)
        resultDisplay = new JTextArea("Please enter an Order ID and click 'Track'.");
        resultDisplay.setEditable(false);
        resultDisplay.setFont(new Font("Monospaced", Font.PLAIN, 14));
        resultDisplay.setBackground(new Color(50, 50, 50));
        resultDisplay.setForeground(Color.WHITE);
        
        // Create a main content panel
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(new Color(34, 34, 34));
        centerPanel.add(inputPanel, BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(resultDisplay), BorderLayout.CENTER);
        
        add(centerPanel, BorderLayout.CENTER);

        // Back Button (SOUTH)
        JButton homeButton = new JButton("Back to Home");
        // This is the correct call
        styleButton(homeButton, new Color(90, 90, 90)); 
        homeButton.addActionListener(e -> navigator.showScreen("home"));
        
        // Put the back button in a panel to keep it neat
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        southPanel.setBackground(new Color(34, 34, 34));
        southPanel.add(homeButton);
        add(southPanel, BorderLayout.SOUTH);
    } // <-- End of constructor

    // --- This method has the correct braces ---
    private void trackOrder() {
        String orderIdToTrack = orderIdField.getText().trim();
        
        if (orderIdToTrack.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an Order ID.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        StringBuilder history = new StringBuilder();
        history.append("--- HISTORY FOR ORDER: ").append(orderIdToTrack).append(" ---\n\n");
        
        int eventCount = 0;

        // Scan the entire blockchain
        for (Block block : CardLayoutPanel.blockchain) {
            for (OrderStatusEvent event : block.transactions) {
                // Check if the event's orderId matches the one we're tracking
                if (event.orderId.equals(orderIdToTrack)) {
                    eventCount++;
                    history.append(eventCount).append(". ").append(event.statusDetails).append("\n");
                }
            }
        }

        if (eventCount == 0) {
            resultDisplay.setText("No history found for Order ID: " + orderIdToTrack);
        } else {
            resultDisplay.setText(history.toString());
        }
    } // <-- End of trackOrder method

    // --- This is the correct styleButton method ---
    private void styleButton(JButton btn, Color color) {
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
    } // <-- End of styleButton method

} // <-- End of class