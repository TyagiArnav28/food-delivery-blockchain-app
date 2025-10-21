package ui;

import core.Navigator;
import core.CardLayoutPanel; 
import javax.swing.*;
import java.awt.*;

public class HomeScreen extends JPanel {
    
    private Navigator navigator; // <-- NEW: Make this a class variable
    private JLabel titleLabel;   // <-- NEW: Make this a class variable
    private JPanel buttonPanel;  // <-- NEW: Make this a class variable

    public HomeScreen(Navigator navigator) {
        this.navigator = navigator; // <-- NEW: Store the navigator
        
        setLayout(new BorderLayout());
        setBackground(new Color(34, 34, 34));

        // Create the label, but set text in refresh()
        titleLabel = new JLabel("Loading...", SwingConstants.CENTER); 
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        add(titleLabel, BorderLayout.CENTER);

        // Create the button panel, but fill it in refresh()
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10)); 
        buttonPanel.setBackground(new Color(34, 34, 34));
        add(buttonPanel, BorderLayout.SOUTH);
    }

 // --- NEW REFRESH METHOD ---
    // This will be called every time the screen is shown
    public void refresh() {
        // 1. Clear all old buttons from the panel
        buttonPanel.removeAll();
        
        // 2. Check the role
        String role = CardLayoutPanel.currentUserRole;

        if (role == null) {
            titleLabel.setText("Error: No role found. Please log in again.");
        } else {
            // 3. Add the correct title and buttons for the role
            switch (role) {
            case "customer":
                titleLabel.setText("Customer Dashboard"); 
                JButton menuButton = new JButton("Go to Menu");
                styleButton(menuButton);
                menuButton.addActionListener(e -> navigator.showScreen("menu"));
                buttonPanel.add(menuButton);

                // --- ADD THIS NEW BUTTON ---
                JButton trackButton = new JButton("Track My Order");
                styleButton(trackButton);
                trackButton.addActionListener(e -> navigator.showScreen("track"));
                buttonPanel.add(trackButton);
                // -------------------------

                break; // Keep the break
                    
                case "restaurant":
                    titleLabel.setText("Restaurant Dashboard"); 
                    JButton restaurantButton = new JButton("Open Restaurant View");
                    styleButton(restaurantButton);
                    restaurantButton.addActionListener(e -> navigator.showScreen("restaurant"));
                    buttonPanel.add(restaurantButton);
                    break;
                    
                case "driver":
                    titleLabel.setText("Driver Dashboard"); 
                    JButton driverButton = new JButton("Open Driver View");
                    styleButton(driverButton);
                    driverButton.addActionListener(e -> navigator.showScreen("driver"));
                    buttonPanel.add(driverButton);
                    break;
            }
            
            // --- ADD LOGOUT BUTTON FOR ALL ROLES ---
            JButton logoutButton = new JButton("Logout");
            styleButton(logoutButton);
            logoutButton.setBackground(new Color(200, 50, 50)); // Make it red
            logoutButton.addActionListener(e -> {
                CardLayoutPanel.currentUserRole = null; // Clear the role
                navigator.showScreen("login"); // Go back to login
            });
            buttonPanel.add(logoutButton);
        }
        
        // 4. Force the panel to update its visuals
        buttonPanel.revalidate();
        buttonPanel.repaint();
    }

    private void styleButton(JButton btn) {
        btn.setBackground(new Color(0, 150, 136));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
    }
}