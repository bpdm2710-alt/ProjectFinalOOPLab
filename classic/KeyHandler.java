package classic;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.concurrent.atomic.AtomicBoolean;

/** Stores simple one-shot input flags for the game loop. */
public class KeyHandler implements KeyListener {
    private final AtomicBoolean leftPressed = new AtomicBoolean(false);
    private final AtomicBoolean rightPressed = new AtomicBoolean(false);
    private final AtomicBoolean downPressed = new AtomicBoolean(false);
    private final AtomicBoolean rotatePressed = new AtomicBoolean(false);
    private final AtomicBoolean rotateCCWPressed = new AtomicBoolean(false);
    private final AtomicBoolean hardDropPressed = new AtomicBoolean(false);
    private final AtomicBoolean restartPressed = new AtomicBoolean(false);
    private final AtomicBoolean menuPressed = new AtomicBoolean(false);
    private final AtomicBoolean pausePressed = new AtomicBoolean(false);

    /** Returns whether the soft-drop key is held. */
    public boolean isDownPressed() {
        return downPressed.get();
    }

    /** Consumes a left-move request. */
    public boolean consumeLeft() {
        return leftPressed.getAndSet(false);
    }

    /** Consumes a right-move request. */
    public boolean consumeRight() {
        return rightPressed.getAndSet(false);
    }

    /** Consumes a rotate request. */
    public boolean consumeRotate() {
        return rotatePressed.getAndSet(false);
    }

    /** Consumes a counterclockwise rotate request. */
    public boolean consumeRotateCCW() {
        return rotateCCWPressed.getAndSet(false);
    }

    /** Consumes a hard-drop request. */
    public boolean consumeHardDrop() {
        return hardDropPressed.getAndSet(false);
    }

    /** Consumes a restart request. */
    public boolean consumeRestart() {
        return restartPressed.getAndSet(false);
    }

    /** Consumes a menu request. */
    public boolean consumeMenu() {
        return menuPressed.getAndSet(false);
    }

    /** Consumes a pause toggle request. */
    public boolean consumePause() {
        return pausePressed.getAndSet(false);
    }

    /** Clears all transient input flags. */
    public void resetTransientInput() {
        leftPressed.set(false);
        rightPressed.set(false);
        downPressed.set(false);
        rotatePressed.set(false);
        rotateCCWPressed.set(false);
        hardDropPressed.set(false);
        restartPressed.set(false);
        menuPressed.set(false);
        pausePressed.set(false);
    }

    /** Unused by this game. */
    @Override
    public void keyTyped(KeyEvent e) {
    }

    /** Stores the pressed key as a simple flag. */
    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_LEFT) {
            leftPressed.set(true);
        } else if (code == KeyEvent.VK_RIGHT) {
            rightPressed.set(true);
        } else if (code == KeyEvent.VK_DOWN) {
            downPressed.set(true);
        } else if (code == KeyEvent.VK_UP || code == KeyEvent.VK_X) {
            rotatePressed.set(true);
        } else if (code == KeyEvent.VK_Z) {
            rotateCCWPressed.set(true);
        } else if (code == KeyEvent.VK_SPACE) {
            hardDropPressed.set(true);
        } else if (code == KeyEvent.VK_R) {
            restartPressed.set(true);
        } else if (code == KeyEvent.VK_P) {
            pausePressed.set(true);
        } else if (code == KeyEvent.VK_ESCAPE) {
            menuPressed.set(true);
        }
    }

    /** Clears held keys when they are released. */
    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_LEFT) {
            leftPressed.set(false);
        } else if (code == KeyEvent.VK_RIGHT) {
            rightPressed.set(false);
        } else if (code == KeyEvent.VK_DOWN) {
            downPressed.set(false);
        } else if (code == KeyEvent.VK_Z) {
            rotateCCWPressed.set(false);
        } else if (code == KeyEvent.VK_ESCAPE) {
            menuPressed.set(false);
        } else if (code == KeyEvent.VK_P) {
            pausePressed.set(false);
        }
    }
}
