package MainMethods;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mino.*;

public class GameManager {
    /**
     * Pieces in {@link #previewQueue} drawn after the immediate next (UI shows next
     * + this count = 5, tetr.io-style).
     */
    public static final int PREVIEW_COUNT = 4;

    /** Duration of line clear flash effect in frames (at 60 FPS). */
    private static final int EFFECT_DURATION_FRAMES = 15;

    // Tetris Guideline: 10 columns × 20 visible rows + 20 buffer rows = 40 total
    public final int WIDTH = 300; // 10 columns × 30px
    public final int HEIGHT = 600; // 20 visible rows × 30px
    public final int BUFFER_ROWS = 20; // Hidden rows above for spawn zone
    public final int TOTAL_ROWS = 40; // Total internal rows

    private final int left_x;
    private final int right_x;
    private final int top_y;
    private final int bottom_y;

    private Mino currentMino;
    private final int MINO_START_X;
    private final int MINO_START_Y;



    private final java.util.Queue<Integer> previewQueue = new java.util.LinkedList<>();

    private final ArrayList<Block> staticBlocks = new ArrayList<>();

    /**
     * The project assumes one live session: a single {@link GameManager} owned by
     * {@link GamePanel}.
     */

    private int dropInterval = 60;

    // Line clear effect
    private boolean effectCounterOn;
    private int effectCounter;
    private final java.util.concurrent.CopyOnWriteArrayList<Integer> effectY = new java.util.concurrent.CopyOnWriteArrayList<>();

    private int level = 1;
    private int lines = 0;
    private int score = 0;
    private int currentMinoType;
    private int nextMinoType;
    private int holdMinoType = -1;
    private boolean holdUsedInTurn;
    private GameState state = GameState.PLAYING;
    private boolean practiceMode = false;

    private final ScoringStrategy scoringStrategy = new GuidelineScoring();
    private final GameRenderer gameRenderer = new GameRenderer();

    public GameManager() {
        this.dropInterval = 60;
        staticBlocks.clear();
        MinoFactory.resetBag();

        left_x = (GamePanel.WIDTH - WIDTH) / 2;
        right_x = left_x + WIDTH;
        top_y = 50;
        bottom_y = top_y + HEIGHT;

        MINO_START_X = left_x + WIDTH / 2 - Block.SIZE;
        MINO_START_Y = top_y + Block.SIZE;

        previewQueue.clear();
        // Initialize queue with enough pieces for current, next, and previews
        int initialSpawns = PREVIEW_COUNT + 2;
        for (int i = 0; i < initialSpawns; i++) {
            previewQueue.add(MinoFactory.getRandomType());
        }

        currentMinoType = previewQueue.poll();
        nextMinoType = previewQueue.poll();

        currentMino = MinoFactory.createByType(this, currentMinoType);
        currentMino.setXY(MINO_START_X, MINO_START_Y);
    }

    /**
     * Get current Mino piece.
     * 
     * @return the currently active tetromino, or null if none
     */
    public Mino getCurrentMino() {
        return currentMino;
    }

    /**
     * Get preview queue of upcoming pieces.
     * 
     * @return unmodifiable list of queue contents
     */
    public java.util.List<Integer> getPreviewQueue() {
        return Collections.unmodifiableList(new ArrayList<>(previewQueue));
    }

    /**
     * Get next Mino type code.
     * 
     * @return piece type (0-6)
     */
    public int getNextMinoType() {
        return nextMinoType;
    }

    /**
     * Get held Mino type code, or -1 if none.
     * 
     * @return piece type (0-6) or -1 if no hold
     */
    public int getHoldMinoType() {
        return holdMinoType;
    }

    /**
     * Get play field bounds.
     * 
     * @return left boundary x coordinate
     */
    public int getLeftX() {
        return left_x;
    }

    /**
     * Get play field bounds.
     * 
     * @return right boundary x coordinate
     */
    public int getRightX() {
        return right_x;
    }

    /**
     * Get play field bounds.
     * 
     * @return top boundary y coordinate
     */
    public int getTopY() {
        return top_y;
    }

    /**
     * Get play field bounds.
     * 
     * @return bottom boundary y coordinate
     */
    public int getBottomY() {
        return bottom_y;
    }

    /**
     * Get current gravity drop interval in frames.
     * 
     * @return drop interval at 60 FPS
     */
    public int getDropInterval() {
        return dropInterval;
    }

    /**
     * Get line clear animation state.
     * 
     * @return true if animation is playing
     */
    public boolean isEffectCounterOn() {
        return effectCounterOn;
    }

    /**
     * Get line clear animation progress counter.
     * 
     * @return animation frame counter
     */
    public int getEffectCounter() {
        return effectCounter;
    }

    /**
     * Get y-coordinates of rows being cleared.
     * 
     * @return thread-safe list of y positions
     */
    public java.util.concurrent.CopyOnWriteArrayList<Integer> getEffectY() {
        return effectY;
    }

    /**
     * Get current level.
     * 
     * @return level (1-based)
     */
    public int getLevel() {
        return level;
    }

    /**
     * Get total lines cleared.
     * 
     * @return line count
     */
    public int getLines() {
        return lines;
    }

    /**
     * Get current score.
     * 
     * @return score points
     */
    public int getScore() {
        return score;
    }

    /**
     * Check if practice mode is enabled.
     * 
     * @return true if gravity is locked at level 1
     */
    public boolean isPracticeMode() {
        return practiceMode;
    }

    /**
     * Set practice mode flag.
     * 
     * @param practiceMode true to lock gravity at starting speed
     */
    public void setPracticeMode(boolean practiceMode) {
        this.practiceMode = practiceMode;
    }

    public List<Block> getStaticBlocks() {
        return Collections.unmodifiableList(staticBlocks);
    }

    /**
     * Guideline-style soft/hard drop bonus (lines still use
     * {@link GuidelineScoring}).
     */
    public void addScore(int points) {
        if (state == GameState.PLAYING && points != 0) {
            score += points;
        }
    }

    public void restartGame() {
        dropInterval = 60;
        staticBlocks.clear();
        effectY.clear();
        effectCounterOn = false;
        effectCounter = 0;
        score = 0;
        lines = 0;
        level = 1;
        holdMinoType = -1;
        holdUsedInTurn = false;
        state = GameState.PLAYING;

        MinoFactory.resetBag();

        previewQueue.clear();
        int initialSpawns = PREVIEW_COUNT + 2;
        for (int i = 0; i < initialSpawns; i++) {
            previewQueue.add(MinoFactory.getRandomType());
        }

        currentMinoType = previewQueue.poll();
        nextMinoType = previewQueue.poll();

        currentMino = MinoFactory.createByType(this, currentMinoType);
        currentMino.setXY(MINO_START_X, MINO_START_Y);
    }

    public GameState getState() {
        return state;
    }

    public void togglePause() {
        if (state == GameState.PLAYING) {
            state = GameState.PAUSED;
        } else if (state == GameState.PAUSED) {
            state = GameState.PLAYING;
        }
    }

    public void update() {
        // Update line clear animation (kept out of renderer to avoid state mutation in
        // paint)
        if (effectCounterOn) {
            effectCounter++;
            if (effectCounter >= EFFECT_DURATION_FRAMES) {
                effectCounter = 0;
                effectCounterOn = false;
                effectY.clear();
            }
        }

        if (KeyHandler.consumeHold()) {
            holdMino();
            KeyHandler.consumeHardDrop(); // discard hard drop if hold was just processed in the same frame
        }

        if (KeyHandler.consumeHardDrop()) {
            hardDropCurrentMino();
        }

        if (currentMino.activeMino == false) {
            staticBlocks.add(currentMino.b[0]);
            staticBlocks.add(currentMino.b[1]);
            staticBlocks.add(currentMino.b[2]);
            staticBlocks.add(currentMino.b[3]);

            currentMino.deactivating = false;
            holdUsedInTurn = false;

            checkDelete();

            if (isSpawnBlocked()) {
                state = GameState.GAME_OVER;
                GamePanel.getMusic().stop();
                GamePanel.getEffect().playEffect(2);
                return;
            }

            spawnNextMino();
        } else {
            currentMino.update();
        }
    }

    public void checkDelete() {
        int y = top_y - BUFFER_ROWS * Block.SIZE;
        int lineCount = 0;
        ArrayList<Integer> linesToClear = new ArrayList<>();

        while (y < bottom_y) {
            int blockCount = 0;

            for (int i = 0; i < staticBlocks.size(); i++) {
                if (staticBlocks.get(i).y == y) {
                    blockCount++;
                }
            }

            if (blockCount == 10) {
                effectCounterOn = true;
                effectY.add(y);
                linesToClear.add(y);

                for (int i = staticBlocks.size() - 1; i > -1; i--) {
                    if (staticBlocks.get(i).y == y) {
                        staticBlocks.remove(i);
                    }
                }

                lineCount++;
            }
            y += Block.SIZE;
        }

        if (lineCount > 0) {
            lines += lineCount;
            if (!practiceMode) {
                recalculateDropInterval();
            }

            for (int i = 0; i < staticBlocks.size(); i++) {
                int shift = 0;
                for (int clearedY : linesToClear) {
                    if (clearedY > staticBlocks.get(i).y) {
                        shift++;
                    }
                }
                staticBlocks.get(i).y += shift * Block.SIZE;
            }

            GamePanel.getEffect().playEffect(1);
            score += scoringStrategy.calculate(lineCount, level);
        }
    }

    private void recalculateDropInterval() {
        level = lines / 5 + 1;
        double secondsPerRow = Math.pow(Math.max(0.01, 0.8 - ((level - 1) * 0.007)), level - 1);
        dropInterval = Math.max(1, (int) (secondsPerRow * GamePanel.FPS));
    }

    public void draw(Graphics2D g2) {
        gameRenderer.draw(this, g2);
    }

    private boolean isSpawnBlocked() {
        Mino test = MinoFactory.createByType(this, nextMinoType);
        test.setXY(MINO_START_X, MINO_START_Y);
        for (Block block : test.b) {
            for (Block sb : staticBlocks) {
                if (block.x == sb.x && block.y == sb.y) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isGameOver() {
        return state == GameState.GAME_OVER;
    }

    private void spawnNextMino() {
        currentMinoType = nextMinoType;
        currentMino = MinoFactory.createByType(this, currentMinoType);
        currentMino.setXY(MINO_START_X, MINO_START_Y);

        nextMinoType = previewQueue.poll();
        previewQueue.add(MinoFactory.getRandomType());
    }

    private void holdMino() {
        if (holdUsedInTurn || currentMino == null) {
            return;
        }

        if (holdMinoType == -1) {
            holdMinoType = currentMinoType;
            spawnNextMino();
        } else {
            int swapType = currentMinoType;
            currentMinoType = holdMinoType;
            holdMinoType = swapType;
            currentMino = MinoFactory.createByType(this, currentMinoType);
            currentMino.setXY(MINO_START_X, MINO_START_Y);
        }

        holdUsedInTurn = true;
    }

    private void hardDropCurrentMino() {
        if (currentMino == null || !currentMino.activeMino) {
            return;
        }

        int dropDistance = calculateDropDistance(currentMino);
        for (int i = 0; i < currentMino.b.length; i++) {
            currentMino.b[i].y += dropDistance * Block.SIZE;
        }
        this.addScore(2 * dropDistance);

        currentMino.deactivating = false;
        currentMino.activeMino = false;
        GamePanel.getEffect().playEffect(4);
    }

    int calculateDropDistance(Mino mino) {
        int minDrop = Integer.MAX_VALUE;

        for (int i = 0; i < mino.b.length; i++) {
            int limitY = bottom_y - Block.SIZE;

            for (int j = 0; j < staticBlocks.size(); j++) {
                Block staticBlock = staticBlocks.get(j);
                if (staticBlock.x == mino.b[i].x && staticBlock.y > mino.b[i].y) {
                    limitY = Math.min(limitY, staticBlock.y - Block.SIZE);
                }
            }

            int dropForBlock = (limitY - mino.b[i].y) / Block.SIZE;
            minDrop = Math.min(minDrop, dropForBlock);
        }

        return Math.max(minDrop, 0);
    }
}
