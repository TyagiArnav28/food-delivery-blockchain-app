package ui;

import core.Navigator;
import core.CardLayoutPanel; // <-- NEW: Import the controller
import javax.swing.*;
import java.awt.*;

public class LoginScreen extends JPanel {

    // --- NEW: Add a reference to the controller ---
    private CardLayoutPanel controller;
    private JTextField usernameField; // Made this a class variable
    private JPasswordField passwordField; // Made this a class variable

    // --- UPDATED CONSTRUCTOR ---
    public LoginScreen(Navigator navigator, CardLayoutPanel controller) {
        this.controller = controller; // <-- NEW
        
        setLayout(new GridBagLayout());
        setBackground(new Color(34, 34, 34)); // dark background

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);

        JLabel title = new JLabel("Welcome to Food Delivery");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(new Color(220, 220, 220));
        gbc.gridy = 0;
        add(title, gbc);

        gbc.gridy++;
        JLabel userLabel = new JLabel("Role (customer, restaurant, or driver):"); // <-- NEW text
        userLabel.setForeground(Color.LIGHT_GRAY);
        add(userLabel, gbc);

        gbc.gridy++;
        usernameField = new JTextField(15); // Use class variable
        usernameField.setBackground(new Color(50, 50, 50));
        usernameField.setForeground(Color.WHITE);
        add(usernameField, gbc);

        gbc.gridy++;
        JLabel passLabel = new JLabel("Password (can be empty):"); // <-- NEW text
        passLabel.setForeground(Color.LIGHT_GRAY);
        add(passLabel, gbc);

        gbc.gridy++;
        passwordField = new JPasswordField(15); // Use class variable
        passwordField.setBackground(new Color(50, 50, 50));
        passwordField.setForeground(Color.WHITE);
        add(passwordField, gbc);

        gbc.gridy++;
        JButton loginBtn = new JButton("Login");
        styleButton(loginBtn);
        
        // --- UPDATED ACTION LISTENER ---
        loginBtn.addActionListener(e -> {
            // This is our new role-checking logic
            String role = usernameField.getText().trim().toLowerCase();
            
            if (role.equals("customer") || role.equals("restaurant") || role.equals("driver")) {
                // Set the role in the main controller
                CardLayoutPanel.currentUserRole = role; 
                // Navigate to the home screen
                navigator.showScreen("home"); 
            } else {
                // Show an error message
                JOptionPane.showMessageDialog(this, 
                    "Invalid Role. Please enter 'customer', 'restaurant', or 'driver'.", 
                    "Login Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        add(loginBtn, gbc);
    }

    private void styleButton(JButton btn) {
        btn.setBackground(new Color(0, 150, 136));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
    }
}