package MainMethods;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.CardLayout;

public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            JFrame window = new JFrame("Tetris");
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setResizable(false);

            JPanel mainContainer = new JPanel(new CardLayout());
            GamePanel gamePanel = new GamePanel();
            CardLayout cl = (CardLayout) mainContainer.getLayout();
            gamePanel.setNavigation(cl, mainContainer);
            MenuPanel menuPanel = new MenuPanel(cl, mainContainer, gamePanel);

            mainContainer.add(menuPanel, "MENU");
            mainContainer.add(gamePanel, "GAME");

            window.add(mainContainer);
            window.pack();

            window.setLocationRelativeTo(null);
            window.setVisible(true);
        });
    }
}