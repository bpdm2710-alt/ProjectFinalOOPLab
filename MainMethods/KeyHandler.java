package MainMethods;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Input uses AtomicBoolean + consume on the game thread to avoid lost updates
 * between key events (EDT) and simulation reads.
 */
public class KeyHandler implements KeyListener {
    private static final AtomicBoolean leftPressed = new AtomicBoolean(false);
    private static final AtomicBoolean rightPressed = new AtomicBoolean(false);
    private static final AtomicBoolean downPressed = new AtomicBoolean(false);
    private static final AtomicBoolean rotateClockwisePressed = new AtomicBoolean(false);
    private static final AtomicBoolean rotateCounterClockwisePressed = new AtomicBoolean(false);
    private static final AtomicBoolean rotateHalfTurnPressed = new AtomicBoolean(false);
    private static final AtomicBoolean hardDropPressed = new AtomicBoolean(false);
    private static final AtomicBoolean holdPressed = new AtomicBoolean(false);
    private static final AtomicBoolean restartPressed = new AtomicBoolean(false);

    public static boolean consumeLeft() {
        return leftPressed.getAndSet(false);
    }

    public static boolean consumeRight() {
        return rightPressed.getAndSet(false);
    }

    public static boolean consumeDown() {
        return downPressed.getAndSet(false);
    }

    public static boolean consumeRotateClockwise() {
        return rotateClockwisePressed.getAndSet(false);
    }

    public static boolean consumeRotateCounterClockwise() {
        return rotateCounterClockwisePressed.getAndSet(false);
    }

    public static boolean consumeRotateHalfTurn() {
        return rotateHalfTurnPressed.getAndSet(false);
    }

    public static boolean consumeHardDrop() {
        return hardDropPressed.getAndSet(false);
    }

    public static boolean consumeHold() {
        return holdPressed.getAndSet(false);
    }

    public static boolean consumeRestart() {
        return restartPressed.getAndSet(false);
    }

    /** Clears one-shot action flags (called after restart). */
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
}
