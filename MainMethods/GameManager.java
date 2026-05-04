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
    public static final int PREVIEW_COUNT = 4;

    /** Minimum gravity interval (frames); avoids division issues and stuck loop at high level. */
    private static final int MIN_DROP_INTERVAL_FRAMES = 1;

    // Tetris Guideline: 10 columns × 20 visible rows + 20 buffer rows = 40 total
    public final int WIDTH = 300;      // 10 columns × 30px
    public final int HEIGHT = 600;     // 20 visible rows × 30px
    public final int BUFFER_ROWS = 20; // Hidden rows above for spawn zone
    public final int TOTAL_ROWS = 40;  // Total internal rows

    public int left_x;
    public int right_x;
    public int top_y;
    public int bottom_y;

    Mino currentMino;
    private final int MINO_START_X;
    private final int MINO_START_Y;
    Mino nextMino;
    private final int NEXTMINO_X;
    private final int NEXTMINO_Y;
    private final int HOLDMINO_X;
    private final int HOLDMINO_Y;

    java.util.Queue<Integer> previewQueue = new java.util.LinkedList<>();

    private final ArrayList<Block> staticBlocks = new ArrayList<>();

    /**
     * The project assumes one live session: a single {@link GameManager} owned by {@link GamePanel}.
     */

    public int dropInterval = 60;

    // Line clear effect - shorter duration for cleaner animation
    boolean effectCounterOn;
    int effectCounter;
    java.util.concurrent.CopyOnWriteArrayList<Integer> effectY = new java.util.concurrent.CopyOnWriteArrayList<>();

    int level = 1;
    int lines = 0;
    int score = 0;
    int currentMinoType;
    int nextMinoType;
    int holdMinoType = -1;
    Mino holdMino;
    boolean holdUsedInTurn;
    GameState state = GameState.PLAYING;
    public boolean practiceMode = false;

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

        NEXTMINO_X = right_x + 140;
        NEXTMINO_Y = top_y + 100;
        HOLDMINO_X = left_x - 210;
        HOLDMINO_Y = top_y + 100;

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
        nextMino = MinoFactory.createByType(this, nextMinoType);
        nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);
    }

    public List<Block> getStaticBlocks() {
        return Collections.unmodifiableList(staticBlocks);
    }

    /** Guideline-style soft/hard drop bonus (lines still use {@link GuidelineScoring}). */
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
        holdMino = null;
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
        nextMino = MinoFactory.createByType(this, nextMinoType);
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
        // Update line clear animation (kept out of renderer to avoid state mutation in paint)
        if (effectCounterOn) {
            effectCounter++;
            if (effectCounter >= 15) {
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
                GamePanel.music.stop();
                GamePanel.effect.playEffect(2);
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
                level = lines / 5 + 1;
                
                double secondsPerRow = Math.pow(Math.max(0.01, 0.8 - ((level - 1) * 0.007)), level - 1);
                dropInterval = Math.max(1, (int)(secondsPerRow * 60));
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

            GamePanel.effect.playEffect(1);
            score += scoringStrategy.calculate(lineCount, level);
        }
    }

    public void draw(Graphics2D g2) {
        gameRenderer.draw(this, g2);
    }

    private boolean isSpawnBlocked() {
        int x = MINO_START_X;
        int y = MINO_START_Y;
        int s = Block.SIZE;
        
        int[] tx = new int[4];
        int[] ty = new int[4];
        
        switch(nextMinoType) {
            case 0: // L
                tx[0]=x; ty[0]=y; tx[1]=x-s; ty[1]=y; tx[2]=x+s; ty[2]=y; tx[3]=x+s; ty[3]=y-s; break;
            case 1: // J
                tx[0]=x; ty[0]=y; tx[1]=x+s; ty[1]=y; tx[2]=x-s; ty[2]=y; tx[3]=x-s; ty[3]=y-s; break;
            case 2: // I
                tx[0]=x; ty[0]=y; tx[1]=x-s; ty[1]=y; tx[2]=x+s; ty[2]=y; tx[3]=x+s*2; ty[3]=y; break;
            case 3: // O
                tx[0]=x; ty[0]=y; tx[1]=x; ty[1]=y+s; tx[2]=x+s; ty[2]=y; tx[3]=x+s; ty[3]=y+s; break;
            case 4: // Z
                tx[0]=x; ty[0]=y; tx[1]=x+s; ty[1]=y; tx[2]=x; ty[2]=y-s; tx[3]=x-s; ty[3]=y-s; break;
            case 5: // T
                tx[0]=x; ty[0]=y; tx[1]=x; ty[1]=y-s; tx[2]=x-s; ty[2]=y; tx[3]=x+s; ty[3]=y; break;
            case 6: // S
                tx[0]=x; ty[0]=y; tx[1]=x+s; ty[1]=y; tx[2]=x-s; ty[2]=y+s; tx[3]=x; ty[3]=y+s; break;
            default:
                tx[0]=x; ty[0]=y; tx[1]=x; ty[1]=y+s; tx[2]=x+s; ty[2]=y; tx[3]=x+s; ty[3]=y+s; break;
        }
        
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < staticBlocks.size(); j++) {
                if (tx[i] == staticBlocks.get(j).x && ty[i] == staticBlocks.get(j).y) {
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

        nextMino = MinoFactory.createByType(this, nextMinoType);
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
            currentMino = MinoFactory.createByType(this, currentMinoType);
            currentMino.setXY(MINO_START_X, MINO_START_Y);
        }

        holdMino = MinoFactory.createByType(this, holdMinoType);
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
        this.addScore(2 * dropDistance);

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
