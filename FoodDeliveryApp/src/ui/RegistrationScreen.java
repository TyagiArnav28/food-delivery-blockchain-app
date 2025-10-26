package ui;

import core.Navigator;
import core.CardLayoutPanel; // <-- NEW: Import controller
import core.DatabaseManager;  // <-- NEW: Import DatabaseManager
import javax.swing.*;
import java.awt.*;
import java.util.Arrays; // <-- NEW: Import Arrays for password comparison

public class RegistrationScreen extends JPanel {

    private Navigator navigator;
    private CardLayoutPanel controller; // <-- NEW: Controller reference
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JComboBox<String> roleComboBox;

    // --- UPDATED CONSTRUCTOR ---
    public RegistrationScreen(Navigator navigator, CardLayoutPanel controller) { // Added controller
        this.navigator = navigator;
        this.controller = controller; // <-- NEW
        
        setLayout(new GridBagLayout());
        setBackground(new Color(34, 34, 34)); // Dark background

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridwidth = 2; 
        gbc.gridx = 0;

        // Title
        JLabel welcomeTitle = new JLabel("Welcome to BlockDrop");
        welcomeTitle.setFont(new Font("SansSerif", Font.BOLD, 30)); // Make it bigger
        welcomeTitle.setForeground(new Color(0, 150, 136)); // Use accent color
        gbc.gridy = 0; // First row
        add(welcomeTitle, gbc);
        
        JLabel subTitle = new JLabel("Register New Account");
        subTitle.setFont(new Font("SansSerif", Font.BOLD, 22)); // Make slightly smaller
        subTitle.setForeground(new Color(220, 220, 220));
        gbc.gridy = 1; // Second row
        add(subTitle, gbc);

        // Role Selection
        gbc.gridwidth = 1; 
        gbc.gridy=2;
        gbc.gridx = 0;
        JLabel roleLabel = new JLabel("Select Role:");
        roleLabel.setForeground(Color.LIGHT_GRAY);
        add(roleLabel, gbc);

        gbc.gridx = 1;
        String[] roles = { "customer", "restaurant", "driver" };
        roleComboBox = new JComboBox<>(roles);
        add(roleComboBox, gbc);

        // Username
        gbc.gridy++;
        gbc.gridx = 0;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(Color.LIGHT_GRAY);
        add(userLabel, gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(15);
        usernameField.setBackground(new Color(50, 50, 50));
        usernameField.setForeground(Color.WHITE);
        add(usernameField, gbc);

        // Password
        gbc.gridy++;
        gbc.gridx = 0;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(Color.LIGHT_GRAY);
        add(passLabel, gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        passwordField.setBackground(new Color(50, 50, 50));
        passwordField.setForeground(Color.WHITE);
        add(passwordField, gbc);

        // Confirm Password
        gbc.gridy++;
        gbc.gridx = 0;
        JLabel confirmPassLabel = new JLabel("Confirm Password:");
        confirmPassLabel.setForeground(Color.LIGHT_GRAY);
        add(confirmPassLabel, gbc);

        gbc.gridx = 1;
        confirmPasswordField = new JPasswordField(15);
        confirmPasswordField.setBackground(new Color(50, 50, 50));
        confirmPasswordField.setForeground(Color.WHITE);
        add(confirmPasswordField, gbc);

        // Buttons Panel
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2; 
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        buttonPanel.setBackground(new Color(34, 34, 34));

        JButton registerBtn = new JButton("Register");
        styleButton(registerBtn);
        // --- NEW: Add action listener ---
        registerBtn.addActionListener(e -> registerUser()); 

        JButton goToLoginBtn = new JButton("Already have account? Login");
        styleButton(goToLoginBtn);
        goToLoginBtn.setBackground(new Color(90, 90, 90)); 
        goToLoginBtn.addActionListener(e -> navigator.showScreen("login"));

        buttonPanel.add(registerBtn);
        buttonPanel.add(goToLoginBtn);
        add(buttonPanel, gbc);
    }

    // --- NEW: Method to handle registration logic ---
    private void registerUser() {
        // 1. Get data from fields
        String selectedRole = (String) roleComboBox.getSelectedItem();
        String username = usernameField.getText().trim();
        char[] passwordChars = passwordField.getPassword();
        char[] confirmPasswordChars = confirmPasswordField.getPassword();

        // 2. Basic Validation
        if (username.isEmpty() || passwordChars.length == 0 || confirmPasswordChars.length == 0) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Registration Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!Arrays.equals(passwordChars, confirmPasswordChars)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.", "Registration Error", JOptionPane.ERROR_MESSAGE);
            // Clear password fields for security
            passwordField.setText("");
            confirmPasswordField.setText("");
            return;
        }

        // 3. Call DatabaseManager to register
        String password = new String(passwordChars); // Convert char[] to String
        boolean success = DatabaseManager.registerUser(username, password, selectedRole);

        // 4. Show result and navigate
        if (success) {
            JOptionPane.showMessageDialog(this, 
                "Registration successful! Please login.", 
                "Registration Success", 
                JOptionPane.INFORMATION_MESSAGE);
            navigator.showScreen("login"); // Go to login screen
        } else {
            JOptionPane.showMessageDialog(this, 
                "Registration failed. Username might already be taken.", 
                "Registration Error", 
                JOptionPane.ERROR_MESSAGE);
        }
        
        // Clear password fields regardless of success/failure
        passwordField.setText("");
        confirmPasswordField.setText("");
    }

    private void styleButton(JButton btn) {
        btn.setBackground(new Color(0, 150, 136));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14)); 
    }
}