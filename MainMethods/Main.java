package MainMethods;

import java.awt.CardLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Main {
    /** Starts the Swing application on the event dispatch thread. */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame window = new JFrame("Classic Tetris");
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setResizable(false);

            CardLayout cardLayout = new CardLayout();
            JPanel container = new JPanel(cardLayout);
            GamePanel gamePanel = new GamePanel();
            gamePanel.setNavigation(cardLayout, container);
            MenuPanel menuPanel = new MenuPanel(cardLayout, container, gamePanel);

            container.add(menuPanel, "MENU");
            container.add(gamePanel, "GAME");

            window.add(container);
            window.pack();
            window.setLocationRelativeTo(null);
            window.setVisible(true);
            cardLayout.show(container, "MENU");
        });
    }
}