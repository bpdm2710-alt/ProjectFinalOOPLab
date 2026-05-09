package MainMethods;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Input: EDT sets flags; game thread consumes with {@code getAndSet(false)}-style methods
 * so key events are not lost between threads.
 */
public class KeyHandler implements KeyListener {
    private final AtomicBoolean leftPressed = new AtomicBoolean(false);
    private final AtomicBoolean rightPressed = new AtomicBoolean(false);
    private final AtomicBoolean downPressed = new AtomicBoolean(false);
    private final AtomicBoolean rotateClockwisePressed = new AtomicBoolean(false);
    private final AtomicBoolean rotateCounterClockwisePressed = new AtomicBoolean(false);
    private final AtomicBoolean rotateHalfTurnPressed = new AtomicBoolean(false);
    private final AtomicBoolean hardDropPressed = new AtomicBoolean(false);
    private final AtomicBoolean holdPressed = new AtomicBoolean(false);
    private final AtomicBoolean restartPressed = new AtomicBoolean(false);
    private final AtomicBoolean pausePressed = new AtomicBoolean(false);
    private final AtomicBoolean escPressed = new AtomicBoolean(false);

    public boolean isLeftPressed() {
        return leftPressed.get();
    }

    public boolean isRightPressed() {
        return rightPressed.get();
    }

    public boolean isDownPressed() {
        return downPressed.get();
    }

    public boolean isEscPressed() {
        return escPressed.get();
    }

    public boolean consumeLeft() {
        return leftPressed.getAndSet(false);
    }

    public boolean consumeRight() {
        return rightPressed.getAndSet(false);
    }

    public boolean consumeDown() {
        return downPressed.getAndSet(false);
    }

    public boolean consumeRotateClockwise() {
        return rotateClockwisePressed.getAndSet(false);
    }

    public boolean consumeRotateCounterClockwise() {
        return rotateCounterClockwisePressed.getAndSet(false);
    }

    public boolean consumeRotateHalfTurn() {
        return rotateHalfTurnPressed.getAndSet(false);
    }

    public boolean consumeHardDrop() {
        return hardDropPressed.getAndSet(false);
    }

    public boolean consumeHold() {
        return holdPressed.getAndSet(false);
    }

    public boolean consumeRestart() {
        return restartPressed.getAndSet(false);
    }

    public boolean consumePause() {
        return pausePressed.getAndSet(false);
    }

    /** Clears one-shot action flags (e.g. after restart). */
    public void resetTransientInput() {
        leftPressed.set(false);
        rightPressed.set(false);
        downPressed.set(false);
        rotateClockwisePressed.set(false);
        rotateCounterClockwisePressed.set(false);
        rotateHalfTurnPressed.set(false);
        hardDropPressed.set(false);
        holdPressed.set(false);
        restartPressed.set(false);
        pausePressed.set(false);
        escPressed.set(false);
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
            pausePressed.set(true);
        }
        if (code == KeyEvent.VK_ESCAPE) {
            escPressed.set(true);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_LEFT) {
            leftPressed.set(false);
        }
        if (code == KeyEvent.VK_RIGHT) {
            rightPressed.set(false);
        }
        if (code == KeyEvent.VK_DOWN) {
            downPressed.set(false);
        }
        if (code == KeyEvent.VK_ESCAPE) {
            escPressed.set(false);
        }
    }
}
