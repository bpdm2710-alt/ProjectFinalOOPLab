package classic;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/** Shows the main menu and starts the selected mode. */
public class MenuPanel extends JPanel {
    private final CardLayout cardLayout;
    private final JPanel mainContainer;
    private final GamePanel gamePanel;

    /** Builds the menu screen. */
    public MenuPanel(CardLayout cardLayout, JPanel mainContainer, GamePanel gamePanel) {
        this.cardLayout = cardLayout;
        this.mainContainer = mainContainer;
        this.gamePanel = gamePanel;

        setPreferredSize(new Dimension(GamePanel.WIDTH, GamePanel.HEIGHT));
        setBackground(new Color(16, 18, 30));
        setLayout(new GridBagLayout());

        JPanel box = new JPanel(new GridLayout(0, 1, 0, 12));
        box.setOpaque(false);

        JLabel title = new JLabel("CLASSIC TETRIS", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 42));
        title.setForeground(Color.WHITE);

        JButton marathonButton = createButton("Marathon");
        JButton practiceButton = createButton("Practice");
        JButton quitButton = createButton("Quit");

        marathonButton.addActionListener(event -> startGame(false));
        practiceButton.addActionListener(event -> startGame(true));
        quitButton.addActionListener(event -> System.exit(0));

        box.add(title);
        box.add(new JLabel("", SwingConstants.CENTER));
        box.add(marathonButton);
        box.add(practiceButton);
        box.add(quitButton);

        add(box, new GridBagConstraints());
    }

    /** Creates one shared button style. */
    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 20));
        button.setFocusPainted(false);
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(40, 56, 92));
        button.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
        return button;
    }

    /** Starts a game and switches to the game card. */
    private void startGame(boolean practiceMode) {
        gamePanel.startGame(practiceMode);
        cardLayout.show(mainContainer, "GAME");
        gamePanel.requestFocusInWindow();
    }
}