package MainMethods;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.concurrent.atomic.AtomicBoolean;

public class KeyHandler implements KeyListener {
    public static final AtomicBoolean leftPressed = new AtomicBoolean(false);
    public static final AtomicBoolean rightPressed = new AtomicBoolean(false);
    public static final AtomicBoolean downPressed = new AtomicBoolean(false);
    public static final AtomicBoolean rotateClockwisePressed = new AtomicBoolean(false);
    public static final AtomicBoolean rotateCounterClockwisePressed = new AtomicBoolean(false);
    public static final AtomicBoolean rotateHalfTurnPressed = new AtomicBoolean(false);
    public static final AtomicBoolean hardDropPressed = new AtomicBoolean(false);
    public static final AtomicBoolean holdPressed = new AtomicBoolean(false);
    public static final AtomicBoolean restartPressed = new AtomicBoolean(false);

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_LEFT) {
            leftPressed.set(true);
        }
        if (code == KeyEvent.VK_RIGHT) {
            rightPressed.set(true);
        }
        if (code == KeyEvent.VK_DOWN) {
            downPressed.set(true);
        }
        if (code == KeyEvent.VK_UP || code == KeyEvent.VK_X) {
            rotateClockwisePressed.set(true);
        }
        if (code == KeyEvent.VK_Z) {
            rotateCounterClockwisePressed.set(true);
        }
        if (code == KeyEvent.VK_A) {
            rotateHalfTurnPressed.set(true);
        }
        if (code == KeyEvent.VK_SPACE) {
            hardDropPressed.set(true);
        }
        if (code == KeyEvent.VK_C) {
            holdPressed.set(true);
        }
        if (code == KeyEvent.VK_R) {
            restartPressed.set(true);
        }
        if (code == KeyEvent.VK_P) {
            GamePanel.togglePause();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    public static void resetTransientInput() {
        leftPressed.set(false);
        rightPressed.set(false);
        downPressed.set(false);
        rotateClockwisePressed.set(false);
        rotateCounterClockwisePressed.set(false);
        rotateHalfTurnPressed.set(false);
        hardDropPressed.set(false);
        holdPressed.set(false);
    }
}
