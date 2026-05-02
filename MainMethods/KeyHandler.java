package MainMethods;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {
    public static boolean leftPressed, rightPressed, downPressed, UpPressed,PausedGame;

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
        if (code == KeyEvent.VK_P){
            if (PausedGame){
                PausedGame = false;
                GamePanel.effect.play(3, false);
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
