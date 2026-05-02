package MainMethods;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;

import mino.*;

public class GameManager {
    final int WIDTH = 360;
    final int HEIGHT = 600;
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
    public static ArrayList<Block> staticBlocks = new ArrayList<>();

    public static int dropInterval = 60;
    boolean gameOver;

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
        dropInterval = 60;
        staticBlocks.clear();

        left_x = (GamePanel.WIDTH - WIDTH) / 2;
        right_x = left_x + WIDTH;
        top_y = 50;
        bottom_y = top_y + HEIGHT;

        MINO_START_X = left_x + WIDTH / 2 - Block.SIZE;
        MINO_START_Y = top_y + Block.SIZE;
        NEXTMINO_X = right_x + 175;
        NEXTMINO_Y = top_y + 200;
        HOLDMINO_X = left_x - 145;
        HOLDMINO_Y = top_y + 200;

        currentMinoType = MinoFactory.getRandomType();
        nextMinoType = MinoFactory.getRandomType();
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
        gameOver = false;
        state = GameState.PLAYING;

        currentMinoType = MinoFactory.getRandomType();
        nextMinoType = MinoFactory.getRandomType();
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
        if (KeyHandler.holdPressed) {
            holdMino();
            KeyHandler.holdPressed = false;
        }

        if (KeyHandler.hardDropPressed) {
            hardDropCurrentMino();
            KeyHandler.hardDropPressed = false;
        }

        if (currentMino.activeMino == false){
            staticBlocks.add(currentMino.b[0]);
            staticBlocks.add(currentMino.b[1]);
            staticBlocks.add(currentMino.b[2]);
            staticBlocks.add(currentMino.b[3]);

            if (isSpawnBlocked()) {
                gameOver = true;
                state = GameState.GAME_OVER;
                GamePanel.music.stop();
                GamePanel.effect.playEffect(1);
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

        // ✅ Count all blocks in this row
        for (int i = 0; i < staticBlocks.size(); i++) {
            if (staticBlocks.get(i).y == y) {
                blockCount++;
            }
        }
        
        if (blockCount == 12) {

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
                dropInterval = (int)(dropInterval * 0.8);
            }

            // Drop blocks above down
            for (int i = 0; i < staticBlocks.size(); i++) {
                if (staticBlocks.get(i).y < y) {
                    staticBlocks.get(i).y += Block.SIZE;
                }
            }
            // ✅ Don't advance y — recheck same row after blocks drop
        } else {
            y += Block.SIZE;
        }
    }

    // Add score once per delete pass, not once per row iteration
    if (lineCount > 0) {
        GamePanel.effect.playEffect(0);
        score += scoringStrategy.calculate(lineCount, level);
    }
}

    public void draw(Graphics2D g2){
        //Main area
        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(4f));
        g2.drawRect(left_x-8, top_y-8, WIDTH+16, HEIGHT+16);

        // preview area
        int x = right_x + 90;
        int y = top_y + 60;
        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(4f));
        g2.drawRect(x, y, 210, 270);
        g2.setFont(new Font("Arial", Font.PLAIN, 20));
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.drawString("NEXT", x + 70, y + 30);

        // hold area
        int holdX = left_x - 260;
        int holdY = top_y + 60;
        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(4f));
        g2.drawRect(holdX, holdY, 210, 270);
        g2.setFont(new Font("Arial", Font.PLAIN, 20));
        g2.drawString("HOLD", holdX + 70, holdY + 30);

        // Draw Scores under preview area
        g2.setFont(new Font("Arial", Font.PLAIN, 18));
        g2.drawString("SCORE: " + score, x, y + 320);
        g2.drawString("LEVEL: " + level, holdX, holdY + 320);
        g2.drawString("LINES: " + lines, holdX, holdY + 350);

        //draw current mino
        if(currentMino != null){
            drawGhostMino(g2);
            currentMino.draw(g2);
        }

        //draw next mino
        drawMiniMino(g2, nextMino, x, y);

        // draw hold mino
        if (holdMino != null) {
            drawMiniMino(g2, holdMino, holdX, holdY);
        }

        //draw static blocks
        for(int i = 0; i < staticBlocks.size(); i++){
            staticBlocks.get(i).draw(g2);
        }
        
        // draw effect
        if (effectCounterOn) {
            effectCounter++;
            g2.setColor(Color.yellow);
            g2.setStroke(new BasicStroke(4f));
            for (int i = 0; i < effectY.size(); i++) {
                int yEffect = effectY.get(i);
                g2.drawLine(left_x, yEffect, right_x, yEffect);
                g2.fillRect(left_x, effectY.get(i), WIDTH, Block.SIZE);
            }
            if (effectCounter > 30) {
                effectCounter = 0;
                effectCounterOn = false;
                effectY.clear();
            }
        }

        //draw pause
        g2.setColor(Color.yellow);
        g2.setFont(g2.getFont().deriveFont(50f));
        if (gameOver) {
            g2.drawString("GAME OVER", GamePanel.WIDTH / 2 - 150, GamePanel.HEIGHT / 2);
            g2.setFont(g2.getFont().deriveFont(30f));
            g2.drawString("Press R to Restart", GamePanel.WIDTH / 2 - 140, GamePanel.HEIGHT / 2 + 50);
        }
        else if (state == GameState.PAUSED){
            g2.drawString("PAUSED", GamePanel.WIDTH / 2 - 100, GamePanel.HEIGHT / 2);
            g2.drawString("Press P again", GamePanel.WIDTH / 2 - 150, GamePanel.HEIGHT / 2 + 60);
        }

        // For Left side info
    }

    private boolean isSpawnBlocked() {
        for (int i = 0; i < staticBlocks.size(); i++) {
            if (staticBlocks.get(i).y <= MINO_START_Y) {
                return true;
            }
        }
        return false;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    private void spawnNextMino() {
        currentMinoType = nextMinoType;
        currentMino = MinoFactory.createByType(currentMinoType);
        currentMino.setXY(MINO_START_X, MINO_START_Y);

        nextMinoType = MinoFactory.getRandomType();
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

        currentMino.deactivating = false;
        currentMino.activeMino = false;
        GamePanel.effect.playEffect(3);
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

    private void drawMiniMino(Graphics2D g2, Mino mino, int boxX, int boxY) {
        if (mino == null) {
            return;
        }

        int previewSize = 18;
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

        int cols = (maxBlockX - minBlockX) / Block.SIZE + 1;
        int rows = (maxBlockY - minBlockY) / Block.SIZE + 1;
        int pieceWidth = cols * previewSize;
        int pieceHeight = rows * previewSize;
        int drawX = boxX + (210 - pieceWidth) / 2;
        int drawY = boxY + 110 + (120 - pieceHeight) / 2;

        for (int i = 0; i < mino.b.length; i++) {
            int offsetX = (mino.b[i].x - minBlockX) / Block.SIZE;
            int offsetY = (mino.b[i].y - minBlockY) / Block.SIZE;
            g2.setColor(mino.b[i].c);
            g2.fillRect(drawX + offsetX * previewSize, drawY + offsetY * previewSize, previewSize - 2, previewSize - 2);
        }
    }
}
