package classic;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

/** Runs the 60 FPS game loop and asks the manager to render everything. */
public class GamePanel extends JPanel implements Runnable, ScoreListener {
    public static final int WIDTH = 640;
    public static final int HEIGHT = 720;
    public static final int FPS = 60;

    final GameManager gameManager;
    private final Sound music = new Sound();
    private final KeyHandler keyHandler = new KeyHandler();
    private volatile boolean running;
    private volatile Thread gameThread;
    private boolean paused;
    private CardLayout cardLayout;
    private JPanel mainContainer;

    /** Builds the playfield panel and wires keyboard input. */
    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(new Color(18, 18, 22));
        setFocusable(true);
        addKeyListener(keyHandler);
        gameManager = new GameManager(keyHandler, music);
        gameManager.addScoreListener(this);
    }

    /** Stores the navigation objects used to return to the menu. */
    public void setNavigation(CardLayout cardLayout, JPanel mainContainer) {
        this.cardLayout = cardLayout;
        this.mainContainer = mainContainer;
    }

    /** Starts or restarts the current game mode. */
    public void startGame(boolean practiceMode) {
        gameManager.reset(practiceMode);
        keyHandler.resetTransientInput();
        paused = false;
        music.playBgm();
        if (!running) {
            running = true;
            gameThread = new Thread(this, "ClassicTetrisLoop");
            gameThread.start();
        }
        requestFocusInWindow();
    }

    /** Returns true while the loop thread is active. */
    public boolean isRunning() {
        return running;
    }

    /** Returns to the menu screen and stops the loop. */
    public void returnToMenu() {
        running = false;
        gameThread = null;
        paused = false;
        music.stopBgm();
        keyHandler.resetTransientInput();
        if (cardLayout != null && mainContainer != null) {
            cardLayout.show(mainContainer, "MENU");
        }
    }

    /** Updates input and game state once per frame. */
    private void update() {
        if (keyHandler.consumeMenu()) {
            returnToMenu();
            return;
        }

        if (keyHandler.consumePause() && !gameManager.isGameOver()) {
            paused = !paused;
            if (!paused) {
                gameManager.refreshTiming();
            }
        }

        if (paused) {
            return;
        }

        if (gameManager.isGameOver()) {
            if (keyHandler.consumeRestart()) {
                gameManager.reset(gameManager.isPracticeMode());
                music.playBgm();
            }
            return;
        }

        gameManager.update();
    }

    /** Paints the board, piece, HUD, and overlay. */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        gameManager.draw(g2);
        if (paused && !gameManager.isGameOver()) {
            drawPauseOverlay(g2);
        }
    }

    /** Draws a pause overlay over the playfield. */
    private void drawPauseOverlay(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, WIDTH, HEIGHT);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 42));
        FontMetrics metrics = g2.getFontMetrics();
        String text = "PAUSED";
        g2.drawString(text, (WIDTH - metrics.stringWidth(text)) / 2, HEIGHT / 2 - 8);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 18));
        String hint = "Press P to resume";
        g2.drawString(hint, (WIDTH - g2.getFontMetrics().stringWidth(hint)) / 2, HEIGHT / 2 + 24);
    }

    /** Runs the frame loop at roughly 60 FPS. */
    @Override
    public void run() {
        long frameLength = 1_000_000_000L / FPS;
        Thread currentThread = Thread.currentThread();
        while (running && gameThread == currentThread) {
            long frameStart = System.nanoTime();
            update();
            repaint();
            long waitTime = frameLength - (System.nanoTime() - frameStart);
            if (waitTime > 0) {
                try {
                    Thread.sleep(waitTime / 1_000_000L, (int) (waitTime % 1_000_000L));
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        running = false;
    }

    /** Receives score updates from the game manager. */
    @Override
    public void onScoreChanged(int score, int level, int lines, int highScore) {
        // Reserved for future HUD extraction.
    }
}
