import core.CardLayoutPanel;
import core.Navigator;
import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Food Delivery App 🍴");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(700, 600);
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);

            // CardLayoutPanel will control all screens
            CardLayoutPanel[] cardPanelHolder = new CardLayoutPanel[1];

            Navigator navigator = (name) -> {
                if (cardPanelHolder[0] != null) {
                    cardPanelHolder[0].show(name);
                }
            };

            CardLayoutPanel cardPanel = new CardLayoutPanel(navigator);
            cardPanelHolder[0] = cardPanel;

            frame.add(cardPanel, BorderLayout.CENTER);
            frame.setVisible(true);
        });
    }
}
