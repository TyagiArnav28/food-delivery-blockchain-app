package ui;

import core.Navigator;
import core.CardLayoutPanel; // <-- NEW: Import the controller
import javax.swing.*;
import java.awt.*;
import core.DatabaseManager;

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

        JLabel title = new JLabel("Welcome to BlockDrop");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(new Color(0, 150, 136));
        gbc.gridy = 0;
        add(title, gbc);

        gbc.gridy++;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(Color.LIGHT_GRAY);
        add(userLabel, gbc);

        gbc.gridy++;
        usernameField = new JTextField(15); // Use class variable
        usernameField.setBackground(new Color(50, 50, 50));
        usernameField.setForeground(Color.WHITE);
        add(usernameField, gbc);

        gbc.gridy++;
        JLabel passLabel = new JLabel("Password:");
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
            // 1. Get the username and password from the fields
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()); // Get password

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Please enter a username and password.", 
                    "Login Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 2. Call our new DatabaseManager to validate the login
            String role = DatabaseManager.validateLogin(username, password);

            // 3. Check the result
            if (role != null) {
                // SUCCESS!
                // Set the role in the main controller
                CardLayoutPanel.currentUserRole = role; 
                // Navigate to the home screen
                navigator.showScreen("home"); 
            } else {
                // FAILURE!
                // Show an error message
                JOptionPane.showMessageDialog(this, 
                    "Invalid username or password.", 
                    "Login Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        add(loginBtn, gbc);
     // --- NEW REGISTRATION BUTTON ---
        gbc.gridy++; // Move to the next row
        JButton goToRegisterBtn = new JButton("New User? Register Here");
        styleButton(goToRegisterBtn);
        goToRegisterBtn.setBackground(new Color(90, 90, 90)); // Use a neutral color
        goToRegisterBtn.addActionListener(e -> navigator.showScreen("register")); // Go to "register"
        add(goToRegisterBtn, gbc);
        // -----------------------------
    }

    private void styleButton(JButton btn) {
        btn.setBackground(new Color(0, 150, 136));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
    }
}