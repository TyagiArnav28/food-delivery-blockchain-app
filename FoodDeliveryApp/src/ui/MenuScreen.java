package ui;

import core.Navigator;
import core.CartItem;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MenuScreen extends JPanel {
    private Navigator navigator;
    private List<CartItem> cartItems;

    public MenuScreen(Navigator navigator, List<CartItem> cartItems) {
        this.navigator = navigator;
        this.cartItems = cartItems;

        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(34, 34, 34));

        JLabel title = new JLabel("🍴 Menu", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        add(title, BorderLayout.NORTH);

        JPanel itemsPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        itemsPanel.setBackground(new Color(34, 34, 34));

        String[][] menu = {
            {"Burger", "7.5"},
            {"Pizza", "8.5"},
            {"Salad", "7.0"},
            {"Pasta", "9.0"},
            {"Sushi", "10.5"}
        };

        for (String[] item : menu) {
            String name = item[0];
            double price = Double.parseDouble(item[1]);

            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            panel.setBackground(new Color(50, 50, 50));
            JLabel label = new JLabel(name + " - $" + price);
            label.setForeground(Color.WHITE);

            JSpinner qtySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
            JButton addButton = new JButton("Add");
            styleButton(addButton);

            addButton.addActionListener(e -> {
                int quantity = (Integer) qtySpinner.getValue();
                boolean found = false;
                for (CartItem c : cartItems) {
                    if (c.name.equals(name)) {
                        c.quantity += quantity;
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    cartItems.add(new CartItem(name, price, quantity));
                }
                JOptionPane.showMessageDialog(this, quantity + " x " + name + " added to cart!");
            });

            panel.add(label);
            panel.add(qtySpinner);
            panel.add(addButton);
            itemsPanel.add(panel);
        }

     // ... (rest of the itemsPanel loop)

        add(new JScrollPane(itemsPanel), BorderLayout.CENTER);

        // --- NEW BUTTON PANEL ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomPanel.setBackground(new Color(34, 34, 34));

        JButton homeBtn = new JButton("⬅ Back to Home");
        styleButton(homeBtn);
        homeBtn.addActionListener(e -> navigator.showScreen("home"));

        JButton viewCartBtn = new JButton("View Cart 🛒");
        styleButton(viewCartBtn);
        viewCartBtn.addActionListener(e -> navigator.showScreen("cart"));

        bottomPanel.add(homeBtn);
        bottomPanel.add(viewCartBtn);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void styleButton(JButton btn) {
        btn.setBackground(new Color(0, 150, 136));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
    }
}
