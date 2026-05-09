package MainMethods;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;
import java.awt.CardLayout;

public class GamePanel extends JPanel implements Runnable {
    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;
    public static final int FPS = 60;
    private volatile Thread gameThread;
    GameManager gameManager;
    private final Sound music = new Sound();
    private final Sound effect = new Sound();
    private final KeyHandler keyHandler = new KeyHandler();

    public Sound getMusic() {
        return music;
    }

    public Sound getEffect() {
        return effect;
    }

    private static final int ESC_HOLD_FRAMES = 30; // 0.5s at 60fps
    private int escHoldCounter = 0;
    private CardLayout cardLayout;
    private JPanel mainContainer;

    public GamePanel() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.black);
        this.setLayout(null);

        this.addKeyListener(keyHandler);
        this.setFocusable(true);

        gameManager = new GameManager(keyHandler, music, effect);
    }

    public void setNavigation(CardLayout cardLayout, JPanel mainContainer) {
        this.cardLayout = cardLayout;
        this.mainContainer = mainContainer;
    }

    public void launchGame() {
        gameThread = new Thread(this);
        gameThread.start();

        music.playAndLoop(0);
    }

    public boolean isRunning() {
        return gameThread != null && gameThread.isAlive();
    }

    public void togglePause() {
        if (gameManager.getState() == GameState.GAME_OVER) {
            return;
        }

        gameManager.togglePause();
        if (gameManager.getState() == GameState.PAUSED) {
            music.pause();
        } else {
            music.resume();
        }
    }

    public void restartGame() {
        gameManager.restartGame();
        music.playAndLoop(0);
    }

    private void update() {
        // ESC hold to return to menu
        if (keyHandler.isEscPressed()) {
            escHoldCounter++;
            if (escHoldCounter >= ESC_HOLD_FRAMES) {
                returnToMenu();
                return;
            }
        } else {
            escHoldCounter = 0;
        }

        if (keyHandler.consumePause()) {
            togglePause();
        }

        if (keyHandler.consumeRestart()) {
            if (gameManager.getState() == GameState.GAME_OVER) {
                restartGame();
                keyHandler.resetTransientInput();
            }
            // else: ignore R while playing — intentionally consumed
        }

        if (gameManager.getState() == GameState.PLAYING) {
            gameManager.update();
        }
    }

    private void returnToMenu() {
        music.stop();
        keyHandler.resetTransientInput();
        escHoldCounter = 0;
        
        Thread temp = gameThread;
        gameThread = null; // Stop the game loop thread
        if (temp != null) {
            try {
                temp.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        if (cardLayout != null && mainContainer != null) {
            cardLayout.show(mainContainer, "MENU");
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        gameManager.draw(g2);
    }

    @Override
    public void run() {
        double DRAW_INTERVAL = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / DRAW_INTERVAL;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }
}
