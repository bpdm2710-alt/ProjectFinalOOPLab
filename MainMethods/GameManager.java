package MainMethods;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mino.*;

public class GameManager {
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

    /** Visible “bag” of upcoming types below the large NEXT preview (must match queue fills). */
    private static final int PREVIEW_COUNT = 3;
    /** Speed curve floor so gravity stays playable at high level. */
    private static final int MIN_DROP_INTERVAL = 1;

    private java.util.Queue<Integer> previewQueue = new java.util.LinkedList<>();

    private static final ArrayList<Block> staticBlocks = new ArrayList<>();

    /** Last GameManager constructed — used for score from {@link mino.Mino}. */
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
    private GameState state = GameState.PLAYING;

    private final ScoringStrategy scoringStrategy = new GuidelineScoring();

    public GameManager(){
        activeInstance = this;
        dropInterval = 60;
        staticBlocks.clear();
        MinoFactory.resetBag(); // Reset piece randomizer on new game

        left_x = (GamePanel.WIDTH - WIDTH) / 2;
        right_x = left_x + WIDTH;
        top_y = 50;
        bottom_y = top_y + HEIGHT;

        // Spawn at top of visible board (rows are indexed from top_y)
        MINO_START_X = left_x + WIDTH / 2 - Block.SIZE;
        MINO_START_Y = top_y + Block.SIZE;
        
        NEXTMINO_X = right_x + 140;
        NEXTMINO_Y = top_y + 100;
        HOLDMINO_X = left_x - 210;
        HOLDMINO_Y = top_y + 100;

        previewQueue.clear();
        for (int i = 0; i < PREVIEW_COUNT; i++) {
            previewQueue.add(MinoFactory.getRandomType());
        }

        currentMinoType = MinoFactory.getRandomType();
        nextMinoType = previewQueue.poll();
        previewQueue.add(MinoFactory.getRandomType());
        
        currentMino = MinoFactory.createByType(currentMinoType);
        currentMino.setXY(MINO_START_X, MINO_START_Y);
        nextMino = MinoFactory.createByType(nextMinoType);
        nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);
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
        
        // Critical: Reset piece randomizer for new game
        MinoFactory.resetBag();

        previewQueue.clear();
        for (int i = 0; i < PREVIEW_COUNT; i++) {
            previewQueue.add(MinoFactory.getRandomType());
        }

        currentMinoType = MinoFactory.getRandomType();
        nextMinoType = previewQueue.poll();
        previewQueue.add(MinoFactory.getRandomType());
        
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

    public void update (){
        if (KeyHandler.consumeHold()) {
            holdMino();
        }

        if (KeyHandler.consumeHardDrop()) {
            hardDropCurrentMino();
        }

        if (currentMino.activeMino == false){
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
        }else {
            currentMino.update();
        }
    }
    public void checkDelete() {
    int y = top_y;
    int lineCount = 0;

    while (y < bottom_y) {
        int blockCount = 0;

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
            for (int i = 0; i < staticBlocks.size(); i++) {
                if (staticBlocks.get(i).y < y) {
                    staticBlocks.get(i).y += Block.SIZE;
                }
            }
            // Don't advance y — recheck same row after blocks drop
        } else {
            y += Block.SIZE;
        }
    }

    // Add score once per delete pass, not once per row iteration
    if (lineCount > 0) {
        GamePanel.effect.playEffect(1);
        score += scoringStrategy.calculate(lineCount, level);
    }
}

    public void draw(Graphics2D g2){
        // Draw main board border
        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(4f));
        g2.drawRect(left_x-8, top_y-8, WIDTH+16, HEIGHT+16);

        // Draw grid on playfield
        drawGrid(g2);

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
            }
        }

        // Draw pause/game over screen
        g2.setColor(Color.yellow);
        
        if (state == GameState.GAME_OVER) {
            // Semi-transparent overlay
            g2.setColor(new Color(0, 0, 0, 150));
            g2.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
            
            g2.setColor(Color.yellow);
            g2.setFont(g2.getFont().deriveFont(60f));
            String gameOverText = "GAME OVER";
            int textWidth = g2.getFontMetrics().stringWidth(gameOverText);
            g2.drawString(gameOverText, GamePanel.WIDTH / 2 - textWidth / 2, GamePanel.HEIGHT / 2 - 40);
            
            g2.setFont(g2.getFont().deriveFont(32f));
            String restartText = "Press R to Restart";
            int restartWidth = g2.getFontMetrics().stringWidth(restartText);
            g2.drawString(restartText, GamePanel.WIDTH / 2 - restartWidth / 2, GamePanel.HEIGHT / 2 + 60);
        }
        else if (state == GameState.PAUSED){
            // Semi-transparent overlay
            g2.setColor(new Color(0, 0, 0, 100));
            g2.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
            
            g2.setColor(Color.yellow);
            g2.setFont(g2.getFont().deriveFont(60f));
            String pausedText = "PAUSED";
            int pausedWidth = g2.getFontMetrics().stringWidth(pausedText);
            g2.drawString(pausedText, GamePanel.WIDTH / 2 - pausedWidth / 2, GamePanel.HEIGHT / 2 - 40);
            
            g2.setFont(g2.getFont().deriveFont(32f));
            String resumeText = "Press P to Resume";
            int resumeWidth = g2.getFontMetrics().stringWidth(resumeText);
            g2.drawString(resumeText, GamePanel.WIDTH / 2 - resumeWidth / 2, GamePanel.HEIGHT / 2 + 60);
        }
    }
    
    private void drawGrid(Graphics2D g2) {
        g2.setColor(new Color(100, 100, 100, 50)); // Semi-transparent gray
        g2.setStroke(new BasicStroke(0.5f));
        
        // Draw vertical grid lines (10 columns)
        for (int x = left_x; x <= right_x; x += Block.SIZE) {
            g2.drawLine(x, top_y, x, bottom_y);
        }
        
        // Draw horizontal grid lines (20 visible rows)
        for (int y = top_y; y <= bottom_y; y += Block.SIZE) {
            g2.drawLine(left_x, y, right_x, y);
        }
    }

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

        GameManager.addScore(2 * dropDistance); // Guideline: hard drop +2 per row

        currentMino.deactivating = false;
        currentMino.activeMino = false;
        GamePanel.effect.playEffect(4);
    }

    private int calculateDropDistance(Mino mino) {
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

    private void drawGhostMino(Graphics2D g2) {
        int dropDistance = calculateDropDistance(currentMino);
        if (dropDistance <= 0) {
            return;
        }

        int margin = 2;
        Color c = currentMino.b[0].c;
        g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 70));
        for (int i = 0; i < currentMino.b.length; i++) {
            g2.fillRect(
                currentMino.b[i].x + margin,
                currentMino.b[i].y + dropDistance * Block.SIZE + margin,
                Block.SIZE - 2 * margin,
                Block.SIZE - 2 * margin
            );
        }
    }

    private void drawMiniMino(Graphics2D g2, Mino mino, int boxX, int boxY, int blockSize) {
        if (mino == null) {
            return;
        }

        // Find the bounding box of the piece
        int minBlockX = mino.b[0].x;
        int maxBlockX = mino.b[0].x;
        int minBlockY = mino.b[0].y;
        int maxBlockY = mino.b[0].y;

        for (int i = 1; i < mino.b.length; i++) {
            minBlockX = Math.min(minBlockX, mino.b[i].x);
            maxBlockX = Math.max(maxBlockX, mino.b[i].x);
            minBlockY = Math.min(minBlockY, mino.b[i].y);
            maxBlockY = Math.max(maxBlockY, mino.b[i].y);
        }

        // Calculate piece dimensions in blocks
        int cols = (maxBlockX - minBlockX) / Block.SIZE + 1;
        int rows = (maxBlockY - minBlockY) / Block.SIZE + 1;
        int pieceWidth = cols * blockSize;
        int pieceHeight = rows * blockSize;
        
        // FIX #3: Proper centering without hardcoded values
        // Center piece within the preview box
        int boxWidth = 180;
        int boxHeight = 80;  // Standard box height for any piece
        
        // Horizontal centering - always center in box width
        int drawX = boxX + (boxWidth - pieceWidth) / 2;
        
        // Vertical centering - center in box height with 5px top padding
        int drawY = boxY + 5 + (boxHeight - pieceHeight) / 2;

        // Draw each block of the piece
        for (int i = 0; i < mino.b.length; i++) {
            int offsetX = (mino.b[i].x - minBlockX) / Block.SIZE;
            int offsetY = (mino.b[i].y - minBlockY) / Block.SIZE;
            g2.setColor(mino.b[i].c);
            g2.fillRect(drawX + offsetX * blockSize, drawY + offsetY * blockSize, blockSize - 2, blockSize - 2);
        }
    }
}
