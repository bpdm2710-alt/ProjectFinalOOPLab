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

    public void lauchGame(){
        launchGame();
    }
    private void update(){
        if (gameManager.isGameOver() && KeyHandler.restartPressed) {
            gameManager = new GameManager();
            KeyHandler.restartPressed = false;
            KeyHandler.PausedGame = false;
            KeyHandler.leftPressed = false;
            KeyHandler.rightPressed = false;
            KeyHandler.downPressed = false;
            KeyHandler.UpPressed = false;
            KeyHandler.hardDropPressed = false;
            KeyHandler.holdPressed = false;
            music.playAndLoop(0);
        }

        if (KeyHandler.PausedGame == false && !gameManager.isGameOver()) {
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
