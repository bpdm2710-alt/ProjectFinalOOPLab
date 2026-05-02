package MainMethods;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {
    public static volatile boolean leftPressed, rightPressed, downPressed;
    public static volatile boolean rotateClockwisePressed, rotateCounterClockwisePressed, rotateHalfTurnPressed;
    public static volatile boolean hardDropPressed, holdPressed, restartPressed;

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_LEFT) {
            leftPressed = true;
        }
        if (code == KeyEvent.VK_RIGHT) {
            rightPressed = true;
        }
        if (code == KeyEvent.VK_DOWN) {
            downPressed = true;
        }
        if (code == KeyEvent.VK_UP || code == KeyEvent.VK_X) {
            rotateClockwisePressed = true;
        }
        if (code == KeyEvent.VK_Z) {
            rotateCounterClockwisePressed = true;
        }
        if (code == KeyEvent.VK_A) {
            rotateHalfTurnPressed = true;
        }
        if (code == KeyEvent.VK_SPACE) {
            hardDropPressed = true;
        }
        if (code == KeyEvent.VK_C) {
            holdPressed = true;
        }
        if (code == KeyEvent.VK_R) {
            restartPressed = true;
        }
        if (code == KeyEvent.VK_P) {
            GamePanel.togglePause();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    public static void resetTransientInput() {
        leftPressed = false;
        rightPressed = false;
        downPressed = false;
        rotateClockwisePressed = false;
        rotateCounterClockwisePressed = false;
        rotateHalfTurnPressed = false;
        hardDropPressed = false;
        holdPressed = false;
    }
}
