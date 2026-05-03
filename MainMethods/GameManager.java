package MainMethods;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mino.*;

public class GameManager {
    /**
     * Pieces in {@link #previewQueue} drawn after the immediate next (UI shows next + this count = 5, tetr.io-style).
     */
    static final int PREVIEW_COUNT = 4;

    /** Minimum gravity interval (frames); avoids division issues and stuck loop at high level. */
    private static final int MIN_DROP_INTERVAL_FRAMES = 1;

    // Tetris Guideline: 10 columns × 20 visible rows + 20 buffer rows = 40 total
    final int WIDTH = 300;      // 10 columns × 30px
    final int HEIGHT = 600;     // 20 visible rows × 30px
    final int BUFFER_ROWS = 20; // Hidden rows above for spawn zone
    final int TOTAL_ROWS = 40;  // Total internal rows

    public static int left_x;
    public static int right_x;
    public static int top_y;
    public static int bottom_y;

    Mino currentMino;
    final int MINO_START_X;
    final int MINO_START_Y;
    Mino nextMino;
    final int NEXTMINO_X;
    final int NEXTMINO_Y;
    final int HOLDMINO_X;
    final int HOLDMINO_Y;

    java.util.Queue<Integer> previewQueue = new java.util.LinkedList<>();

    private final ArrayList<Block> staticBlocks = new ArrayList<>();

    /**
     * {@link #getStaticBlocks()} and {@link #addScore(int)} resolve through this handle.
     * The project assumes one live session: a single {@link GameManager} owned by {@link GamePanel}.
     * Constructing a second instance would repoint this field and orphan the previous game state.
     */
    private static GameManager activeInstance;

    public static int dropInterval = 60;

    // Line clear effect - shorter duration for cleaner animation
    boolean effectCounterOn;
    int effectCounter;
    ArrayList<Integer> effectY = new ArrayList<>();

    int level = 1;
    int lines = 0;
    int score = 0;
    int currentMinoType;
    int nextMinoType;
    int holdMinoType = -1;
    Mino holdMino;
    boolean holdUsedInTurn;
    GameState state = GameState.PLAYING;

    private final ScoringStrategy scoringStrategy = new GuidelineScoring();
    private final GameRenderer gameRenderer = new GameRenderer();

    public GameManager() {
        activeInstance = this;
        dropInterval = 60;
        staticBlocks.clear();
        MinoFactory.resetBag();

        left_x = (GamePanel.WIDTH - WIDTH) / 2;
        right_x = left_x + WIDTH;
        top_y = 50;
        bottom_y = top_y + HEIGHT;

        MINO_START_X = left_x + WIDTH / 2 - Block.SIZE;
        MINO_START_Y = top_y + Block.SIZE;

        NEXTMINO_X = right_x + 140;
        NEXTMINO_Y = top_y + 100;
        HOLDMINO_X = left_x - 210;
        HOLDMINO_Y = top_y + 100;

        previewQueue.clear();
        for (int i = 0; i < PREVIEW_COUNT + 2; i++) {
            previewQueue.add(MinoFactory.getRandomType());
        }

        currentMinoType = previewQueue.poll();
        nextMinoType = previewQueue.poll();

        currentMino = MinoFactory.createByType(currentMinoType);
        currentMino.setXY(MINO_START_X, MINO_START_Y);
        nextMino = MinoFactory.createByType(nextMinoType);
        nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);
    }

    public static List<Block> getStaticBlocks() {
        if (activeInstance == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(activeInstance.staticBlocks);
    }

    /** Guideline-style soft/hard drop bonus (lines still use {@link GuidelineScoring}). */
    public static void addScore(int points) {
        if (activeInstance != null && activeInstance.state == GameState.PLAYING && points != 0) {
            activeInstance.score += points;
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
        holdMino = null;
        holdUsedInTurn = false;
        state = GameState.PLAYING;

        MinoFactory.resetBag();

        previewQueue.clear();
        for (int i = 0; i < PREVIEW_COUNT + 2; i++) {
            previewQueue.add(MinoFactory.getRandomType());
        }

        currentMinoType = previewQueue.poll();
        nextMinoType = previewQueue.poll();

        currentMino = MinoFactory.createByType(currentMinoType);
        currentMino.setXY(MINO_START_X, MINO_START_Y);
        nextMino = MinoFactory.createByType(nextMinoType);
        nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);
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
        if (KeyHandler.consumeHold()) {
            holdMino();
        }

        if (KeyHandler.consumeHardDrop()) {
            hardDropCurrentMino();
        }

        if (currentMino.activeMino == false) {
            staticBlocks.add(currentMino.b[0]);
            staticBlocks.add(currentMino.b[1]);
            staticBlocks.add(currentMino.b[2]);
            staticBlocks.add(currentMino.b[3]);

            if (isSpawnBlocked()) {
                state = GameState.GAME_OVER;
                GamePanel.music.stop();
                GamePanel.effect.playEffect(2);
                return;
            }

            currentMino.deactivating = false;
            holdUsedInTurn = false;

            spawnNextMino();

            checkDelete();
        } else {
            currentMino.update();
        }
    }

    public void checkDelete() {
        int y = top_y;
        int lineCount = 0;

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

                for (int i = staticBlocks.size() - 1; i > -1; i--) {
                    if (staticBlocks.get(i).y == y) {
                        staticBlocks.remove(i);
                    }
                }

                lineCount++;
                lines++;
                level = lines / 5 + 1;
                if (lines % 5 == 0) {
                    dropInterval = Math.max(MIN_DROP_INTERVAL_FRAMES, (int) (dropInterval * 0.8));
                }

                for (int i = 0; i < staticBlocks.size(); i++) {
                    if (staticBlocks.get(i).y < y) {
                        staticBlocks.get(i).y += Block.SIZE;
                    }
                }
            } else {
                y += Block.SIZE;
            }
        }

        if (lineCount > 0) {
            GamePanel.effect.playEffect(1);
            score += scoringStrategy.calculate(lineCount, level);
        }
    }

    public void draw(Graphics2D g2) {
        gameRenderer.draw(this, g2);
    }

    /**
     * Game over only if the next tetromino cannot spawn: any of its spawn cells overlap locked blocks.
     */
    private boolean isSpawnBlocked() {
        Mino test = MinoFactory.createByType(nextMinoType);
        test.setXY(MINO_START_X, MINO_START_Y);
        for (int i = 0; i < test.b.length; i++) {
            for (int j = 0; j < staticBlocks.size(); j++) {
                if (test.b[i].x == staticBlocks.get(j).x && test.b[i].y == staticBlocks.get(j).y) {
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
        currentMino = MinoFactory.createByType(currentMinoType);
        currentMino.setXY(MINO_START_X, MINO_START_Y);

        nextMinoType = previewQueue.poll();
        previewQueue.add(MinoFactory.getRandomType());

        nextMino = MinoFactory.createByType(nextMinoType);
        nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);
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
            currentMino = MinoFactory.createByType(currentMinoType);
            currentMino.setXY(MINO_START_X, MINO_START_Y);
        }

        holdMino = MinoFactory.createByType(holdMinoType);
        holdMino.setXY(HOLDMINO_X, HOLDMINO_Y);
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

        GameManager.addScore(2 * dropDistance);

        currentMino.deactivating = false;
        currentMino.activeMino = false;
        GamePanel.effect.playEffect(4);
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
