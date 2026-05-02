package MainMethods;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {
    public static volatile boolean leftPressed, rightPressed, downPressed, UpPressed, PausedGame;
    public static volatile boolean hardDropPressed, holdPressed, restartPressed;

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int code =  e.getKeyCode();
        if (code == KeyEvent.VK_LEFT){
            leftPressed = true;
        }
        if (code == KeyEvent.VK_RIGHT){
            rightPressed = true;
        }
        if (code == KeyEvent.VK_DOWN){
            downPressed = true;
        }
        if (code == KeyEvent.VK_UP){
            UpPressed = true;
        }
        if (code == KeyEvent.VK_SPACE){
            hardDropPressed = true;
        }
        if (code == KeyEvent.VK_C || code == KeyEvent.VK_SHIFT){
            holdPressed = true;
        }
        if (code == KeyEvent.VK_R){
            restartPressed = true;
        }
        if (code == KeyEvent.VK_P){
            if (PausedGame){
                PausedGame = false;
                GamePanel.effect.play(3, false);
                GamePanel.music.play(0, true);
                GamePanel.music.loop();
            } else {
                PausedGame = true;
                GamePanel.music.stop();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
    
}
