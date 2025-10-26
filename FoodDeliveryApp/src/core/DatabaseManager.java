package core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseManager {

    // Database connection details
    // We'll connect to the database we created
    private static final String DB_URL = "jdbc:mysql://localhost:3306/food_delivery_db";
    private static final String DB_USER = "root";
    
    // -----------------------------------------------------------------
    //  IMPORTANT: Change this to the root password you set
    // -----------------------------------------------------------------
    private static final String DB_PASSWORD = "Anamika@6020"; // Or "password123", etc.

    /**
     * Attempts to establish a connection to the database.
     */
    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    /**
     * Registers a new user in the database.
     * This method hashes the password before storing it.
     */
    public static boolean registerUser(String username, String password, String role) {
        // Hash the password using the same SHA-256 utility we already have
        String hashedPassword = StringUtil.applySha256(password);

        // This is a prepared SQL statement to prevent SQL injection
        String sql = "INSERT INTO users (username, password_hash, role) VALUES (?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, role);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0; // Was the insert successful?

        } catch (SQLException e) {
            // This will often fail if the username is already taken (due to UNIQUE constraint)
            System.out.println("Error registering user: " + e.getMessage());
            return false;
        }
    }

    /**
     * Validates a user's login credentials.
     * Returns the user's role if successful, or null if login fails.
     */
    public static String validateLogin(String username, String password) {
        // Hash the password the user typed, so we can compare it to the hash in the DB
        String hashedPassword = StringUtil.applySha256(password);
        
        String sql = "SELECT role FROM users WHERE username = ? AND password_hash = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Success! Return the user's role (e.g., "customer")
                    return rs.getString("role");
                } else {
                    // No user found with that username/password combo
                    return null;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error validating login: " + e.getMessage());
            return null;
        }
    }
}