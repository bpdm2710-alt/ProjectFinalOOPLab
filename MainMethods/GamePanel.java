package MainMethods;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable{
    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;
    final int FPS = 60;
    Thread gameThread;
    GameManager gameManager;
    public static Sound music = new Sound();
    public static Sound effect = new Sound();

    public GamePanel(){
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.black);
        this.setLayout(null);

        this.addKeyListener(new KeyHandler());
        this.setFocusable(true);

        gameManager = new GameManager();
    }
    public void launchGame(){
        gameThread = new Thread(this);
        gameThread.start();

        music.playAndLoop(0);
    }

    public void togglePause(){
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

    public void restartGame(){
        gameManager.restartGame();
        music.playAndLoop(0);
    }

    private void update(){
        if (KeyHandler.consumePause()) {
            togglePause();
        }

        if (gameManager.getState() != GameState.GAME_OVER) {
            KeyHandler.consumeRestart();
        }

        if (gameManager.getState() == GameState.GAME_OVER && KeyHandler.consumeRestart()) {
            restartGame();
            KeyHandler.resetTransientInput();
        }

        if (gameManager.getState() == GameState.PLAYING) {
            gameManager.update();
        }
    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        gameManager.draw(g2);
    }

    @Override
    public void run() {
        double DRAW_INTERVAL = 1000000000/FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        while(gameThread != null){
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / DRAW_INTERVAL;
            lastTime = currentTime;

            if(delta >= 1){
                update();
                repaint();
                delta--;
            }
        }
    }
}
