package MainMethods;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
<<<<<<< HEAD

import java.awt.Graphics2D;
=======
>>>>>>> c0d029263a687911de201d4d08322d168074970c

import mino.*;

public class GameManager {
    /** Visible next-piece previews below the large “next” mino (must match queue usage). */
    static final int PREVIEW_COUNT = 3;

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

<<<<<<< HEAD
    java.util.Queue<Integer> previewQueue = new java.util.LinkedList<>();

    private final ArrayList<Block> staticBlocks = new ArrayList<>();

=======
    /** Visible “bag” of upcoming types below the large NEXT preview (must match queue fills). */
    private static final int PREVIEW_COUNT = 3;
    /** Speed curve floor so gravity stays playable at high level. */
    private static final int MIN_DROP_INTERVAL = 1;

    private java.util.Queue<Integer> previewQueue = new java.util.LinkedList<>();

    private static final ArrayList<Block> staticBlocks = new ArrayList<>();

    /** Last GameManager constructed — used for score from {@link mino.Mino}. */
>>>>>>> c0d029263a687911de201d4d08322d168074970c
    private static GameManager activeInstance;

    public static int dropInterval = 60;

    public static List<Block> getStaticBlocks() {
        return Collections.unmodifiableList(staticBlocks);
    }

    public static void addScore(int delta) {
        if (activeInstance != null) {
            activeInstance.score += delta;
        }
    }

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

<<<<<<< HEAD
    public GameManager() {
=======
    public GameManager(){
>>>>>>> c0d029263a687911de201d4d08322d168074970c
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
<<<<<<< HEAD
        for (int i = 0; i < PREVIEW_COUNT + 2; i++) {
            previewQueue.add(MinoFactory.getRandomType());
        }

        currentMinoType = previewQueue.poll();
        nextMinoType = previewQueue.poll();

=======
        for (int i = 0; i < PREVIEW_COUNT; i++) {
            previewQueue.add(MinoFactory.getRandomType());
        }

        currentMinoType = MinoFactory.getRandomType();
        nextMinoType = previewQueue.poll();
        previewQueue.add(MinoFactory.getRandomType());
        
>>>>>>> c0d029263a687911de201d4d08322d168074970c
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
<<<<<<< HEAD
        for (int i = 0; i < PREVIEW_COUNT + 2; i++) {
            previewQueue.add(MinoFactory.getRandomType());
        }

        currentMinoType = previewQueue.poll();
        nextMinoType = previewQueue.poll();

=======
        for (int i = 0; i < PREVIEW_COUNT; i++) {
            previewQueue.add(MinoFactory.getRandomType());
        }

        currentMinoType = MinoFactory.getRandomType();
        nextMinoType = previewQueue.poll();
        previewQueue.add(MinoFactory.getRandomType());
        
>>>>>>> c0d029263a687911de201d4d08322d168074970c
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

<<<<<<< HEAD
    public void update() {
        if (KeyHandler.holdPressed.getAndSet(false)) {
            holdMino();
        }

        if (KeyHandler.hardDropPressed.getAndSet(false)) {
=======
    public void update (){
        if (KeyHandler.consumeHold()) {
            holdMino();
        }

        if (KeyHandler.consumeHardDrop()) {
>>>>>>> c0d029263a687911de201d4d08322d168074970c
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

<<<<<<< HEAD
=======
        // Count all blocks in this row
        for (int i = 0; i < staticBlocks.size(); i++) {
            if (staticBlocks.get(i).y == y) {
                blockCount++;
            }
        }
        
        // Check for full row (10 blocks in a 10-column board)
        if (blockCount == 10) {

            effectCounterOn = true;
            effectY.add(y);

            // Remove the full row
            for (int i = staticBlocks.size() - 1; i > -1; i--) {
                if (staticBlocks.get(i).y == y) {
                    staticBlocks.remove(i);
                }
            }

            lineCount++;
            lines++;
            level = lines / 5 + 1;
            // Drop speed - by tetr.io
            // if the level increases, increase the drop speed speed
            if (lines % 5 == 0) {
                dropInterval = Math.max(MIN_DROP_INTERVAL, (int) (dropInterval * 0.8));
            }

            // Drop blocks above down
>>>>>>> c0d029263a687911de201d4d08322d168074970c
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

<<<<<<< HEAD
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
=======
        // Preview area (large NEXT + PREVIEW_COUNT stacked previews)
        int previewX = right_x + 80;
        int previewY = top_y + 60;
        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(4f));
        g2.drawRect(previewX, previewY, 180, 400);
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        // Center "NEXT" text (box width 180, so center at +90)
        g2.drawString("NEXT", previewX + 65, previewY + 28);

        // Hold area
        int holdX = left_x - 220;
        int holdY = top_y + 60;
        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(4f));
        g2.drawRect(holdX, holdY, 180, 180);
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        // Center "HOLD" text (box width 180, so center at +90)
        g2.drawString("HOLD", holdX + 60, holdY + 28);

        // Draw Scores - better positioning
        g2.setFont(new Font("Arial", Font.PLAIN, 16));
        g2.setColor(Color.white);
        g2.drawString("SCORE: " + score, previewX + 5, previewY + 425);
        g2.drawString("LEVEL: " + level, holdX + 5, holdY + 210);
        g2.drawString("LINES: " + lines, holdX + 5, holdY + 240);

        // Draw current mino and ghost
        if(currentMino != null){
            drawGhostMino(g2);
            currentMino.draw(g2);
        }

        // Draw next piece (larger preview)
        drawMiniMino(g2, nextMino, previewX, previewY, 60);

        // Draw preview queue (stacked mini previews)
        java.util.List<Integer> queueList = new java.util.ArrayList<>(previewQueue);
        int queueStartY = previewY + 110;
        for (int i = 0; i < Math.min(PREVIEW_COUNT, queueList.size()); i++) {
            Mino previewPiece = MinoFactory.createByType(queueList.get(i));
            drawMiniMino(g2, previewPiece, previewX, queueStartY + i * 85, 40);
        }

        // Draw hold mino
        if (holdMino != null) {
            drawMiniMino(g2, holdMino, holdX, holdY, 50);
        }

        // Draw static blocks
        for(int i = 0; i < staticBlocks.size(); i++){
            staticBlocks.get(i).draw(g2);
        }
        
        // Draw line clear effect (cleaner - shorter duration and fade)
        if (effectCounterOn) {
            effectCounter++;
            
            // Fade effect: alpha decreases from 255 to 0
            int maxDuration = 15; // Shorter duration for cleaner effect
            float alpha = 1.0f - (float)effectCounter / maxDuration;
            
            // Draw semi-transparent yellow flash over cleared lines
            for (int i = 0; i < effectY.size(); i++) {
                int yEffect = effectY.get(i);
                g2.setColor(new Color(1.0f, 1.0f, 0.0f, Math.max(0, alpha)));
                g2.fillRect(left_x, yEffect, WIDTH, Block.SIZE);
            }
            
            if (effectCounter >= maxDuration) {
                effectCounter = 0;
                effectCounterOn = false;
                effectY.clear();
>>>>>>> c0d029263a687911de201d4d08322d168074970c
            }
        }

        if (lineCount > 0) {
            GamePanel.effect.playEffect(1);
            score += scoringStrategy.calculate(lineCount, level);
        }
    }

<<<<<<< HEAD
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
=======
    /**
     * Game over only if the next piece cannot spawn — overlap between its spawn cells and the stack.
     * (Any static block at y ≤ spawn row in another column is still legal.)
     */
    private boolean isSpawnBlocked() {
        Mino probe = MinoFactory.createByType(nextMinoType);
        probe.setXY(MINO_START_X, MINO_START_Y);
        for (int i = 0; i < probe.b.length; i++) {
            int px = probe.b[i].x;
            int py = probe.b[i].y;
            for (int j = 0; j < staticBlocks.size(); j++) {
                Block s = staticBlocks.get(j);
                if (s.x == px && s.y == py) {
>>>>>>> c0d029263a687911de201d4d08322d168074970c
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
<<<<<<< HEAD

=======
        
>>>>>>> c0d029263a687911de201d4d08322d168074970c
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

<<<<<<< HEAD
        addScore(2 * dropDistance);
=======
        GameManager.addScore(2 * dropDistance); // Guideline: hard drop +2 per row
>>>>>>> c0d029263a687911de201d4d08322d168074970c

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
