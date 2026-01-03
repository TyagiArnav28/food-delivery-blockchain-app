# BlockDrop: Blockchain-Based Food Delivery App 🍔⛓️

**BlockDrop** is a robust Java Swing application that simulates a secure food delivery ecosystem. It integrates **Blockchain technology** to create an immutable ledger for every order transaction and utilizes **MySQL** for secure, role-based user authentication.

## 🚀 Key Features

* **Role-Based Access Control (RBAC):** Distinct interfaces and permissions for Customers, Restaurants, and Drivers.
* **Secure Authentication:** User credentials are stored in a local MySQL database with **SHA-256 password hashing**.
* **Blockchain Integration:** Every order event (Placed, Preparing, Ready, Delivered) is cryptographically hashed and linked to the previous block, ensuring data integrity.
* **Real-Time Simulation:** Updates across different user panels simulate a real-world delivery environment.
* **Digital Wallet:** Simulates transaction processing and balance management.

## 🛠️ Tech Stack

* **Language:** Java (JDK 21+)
* **GUI Framework:** Java Swing (GridBagLayout, CardLayout)
* **Database:** MySQL 8.0 (JDBC for connectivity)
* **Cryptography:** SHA-256 (via Bouncy Castle Provider)
* **Build Tool:** Maven (managed dependencies)

---

## ⚙️ Setup & Installation

### 1. Prerequisites
Ensure you have the following installed:
* Java Development Kit (JDK 21 or higher)
* Eclipse IDE (or IntelliJ IDEA)
* MySQL Server & MySQL Workbench

### 2. Database Configuration
1.  Open **MySQL Workbench**.
2.  Run the following SQL commands to set up the environment:
    ```sql
    CREATE DATABASE food_delivery_db;
    
    USE food_delivery_db;
    
    CREATE TABLE users (
        id INT AUTO_INCREMENT PRIMARY KEY,
        username VARCHAR(50) NOT NULL UNIQUE,
        password_hash VARCHAR(255) NOT NULL,
        role VARCHAR(20) NOT NULL
    );
    ```

### 3. Application Configuration
1.  Clone this repository.
2.  Open `src/core/DatabaseManager.java`.
3.  Update the `DB_PASSWORD` constant with your local MySQL root password:
    ```java
    private static final String DB_PASSWORD = "YOUR_MYSQL_PASSWORD_HERE";
    ```

### 4. Running the App
1.  Right-click `src/Main.java` and select **Run As > Java Application**.
2.  The application will automatically initialize the Blockchain (Genesis Block) and populate default users if the database is empty.

---

## 📖 How to Use (Workflow)

1.  **Registration:** Launch the app and select "Register Here" to create a new account (Customer, Restaurant, or Driver).
2.  **Login:** Use your credentials to log in.
3.  **Customer Flow:**
    * Browse the menu and add items to the cart.
    * Checkout to place an order (this creates a new Block).
    * Go to "Track Order" to see live updates.
4.  **Restaurant Flow:**
    * View incoming orders.
    * Update status to "Preparing" -> "Ready for Pickup".
5.  **Driver Flow:**
    * Accept orders that are ready.
    * Mark orders as "Delivered" to complete the chain.

---

## 👤 Default Test Accounts

If you don't want to register, you can use these pre-configured accounts:

| Role | Username | Password |
| :--- | :--- | :--- |
| **Customer** | `customer` | `pass123` |
| **Restaurant** | `restaurant` | `pass123` |
| **Driver** | `driver` | `pass123` |

---

## 🔮 Future Improvements

* **Networked Database:** Migrate from localhost to a cloud-based SQL server (AWS RDS).
* **P2P Network:** Implement actual peer-to-peer nodes instead of a centralized simulation.
* **Payment Gateway:** Integrate Stripe or PayPal API for real transactions.

---
