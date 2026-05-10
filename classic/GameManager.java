package classic;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Random;

/** Stores the board, the falling piece, scoring, and rendering. */
public class GameManager {
    private static final Path HIGH_SCORE_FILE = Path.of("highscore.txt");
    private static final int COLS = 10;
    private static final int ROWS = 20;
    private static final int CELL = 30;
    private static final int BOARD_X = 50;
    private static final int BOARD_Y = 60;
    private static final int HUD_X = 380;
    private static final long BASE_INTERVAL = 500L;
    private static final long SOFT_DROP_INTERVAL = 50L;

    private final int[][] board = new int[COLS][ROWS];
    private final KeyHandler keyHandler;
    private final Sound sound;
    private final Random random = new Random();
    private final ScoringStrategy scoringStrategy = new GuidelineScoring();
    private final Color[] colors = { Color.CYAN, Color.YELLOW, new Color(170, 80, 220), Color.GREEN, Color.RED,
            Color.BLUE, Color.ORANGE };
    private final List<ScoreListener> scoreListeners = new ArrayList<>();

    private Tetromino currentPiece;
    private Tetromino nextPiece;
    private int pieceX;
    private int pieceY;
    private int score;
    private int highScore;
    private int level;
    private int lines;
    private boolean practiceMode;
    private boolean gameOver;
    private long fallAccumulator;
    private long lastUpdateTime;
    private static final int FLASH_DURATION = 10;
    private final List<Integer> flashingRows = new ArrayList<>();
    private int flashCounter = 0;

    /** Creates the manager and prepares the first game. */
    public GameManager(KeyHandler keyHandler, Sound sound) {
        this.keyHandler = keyHandler;
        this.sound = sound;
        reset(false);
    }

    /** Resets the whole game state and spawns fresh pieces. */
    public void reset(boolean practiceMode) {
        this.practiceMode = practiceMode;
        score = 0;
        level = 1;
        lines = 0;
        gameOver = false;
        fallAccumulator = 0;
        flashingRows.clear();
        flashCounter = 0;
        lastUpdateTime = System.currentTimeMillis();
        highScore = loadHighScore();
        for (int col = 0; col < COLS; col++) {
            for (int row = 0; row < ROWS; row++) {
                board[col][row] = 0;
            }
        }
        currentPiece = TetrominoFactory.createRandom(random);
        nextPiece = TetrominoFactory.createRandom(random);
        pieceX = 3;
        pieceY = 0;
        notifyScoreListeners();
    }

    /** Registers a listener that receives score, level, and line updates. */
    public void addScoreListener(ScoreListener listener) {
        if (listener != null && !scoreListeners.contains(listener)) {
            scoreListeners.add(listener);
            listener.onScoreChanged(score, level, lines, highScore);
        }
    }

    /** Refreshes timing after a pause or flash so gravity does not jump. */
    public void refreshTiming() {
        fallAccumulator = 0;
        lastUpdateTime = System.currentTimeMillis();
    }

    /** Updates input, gravity, and piece locking. */
    public void update() {
        if (gameOver) {
            return;
        }

        // Handle line clear flash animation
        if (!flashingRows.isEmpty()) {
            flashCounter++;
            if (flashCounter >= FLASH_DURATION) {
                performLineClear();
                flashingRows.clear();
                flashCounter = 0;
                lastUpdateTime = System.currentTimeMillis();
                fallAccumulator = 0;
                // After flash completes, spawn next piece
                if (!spawnNextPiece()) {
                    gameOver = true;
                    saveHighScore();
                    sound.playGameOver();
                }
            }
            return;  // Don't process gravity/input while flashing
        }

        long now = System.currentTimeMillis();
        fallAccumulator += now - lastUpdateTime;
        lastUpdateTime = now;

        handleInput();
        if (gameOver) {
            return;
        }

        long interval = keyHandler.isDownPressed() ? SOFT_DROP_INTERVAL : getGravityInterval();
        while (fallAccumulator >= interval && !gameOver) {
            if (movePiece(0, 1)) {
                fallAccumulator -= interval;
            } else {
                lockPiece();
                fallAccumulator = 0;
            }
        }
    }

    /** Handles movement, rotation, and hard drop keys. */
    private void handleInput() {
        if (keyHandler.consumeLeft()) {
            movePiece(-1, 0);
        }
        if (keyHandler.consumeRight()) {
            movePiece(1, 0);
        }
        if (keyHandler.consumeRotate()) {
            rotatePiece();
        }
        if (keyHandler.consumeHardDrop()) {
            hardDropPiece();
        }
    }

    /** Tries to move the current piece by the given offset. */
    private boolean movePiece(int dx, int dy) {
        if (canPlace(currentPiece, pieceX + dx, pieceY + dy, currentPiece.getRotation())) {
            pieceX += dx;
            pieceY += dy;
            return true;
        }
        return false;
    }

    /** Rotates the current piece and reverts if the new position collides. */
    private void rotatePiece() {
        int previousRotation = currentPiece.getRotation();
        currentPiece.rotateClockwise();
        if (!canPlace(currentPiece, pieceX, pieceY, currentPiece.getRotation())) {
            currentPiece.setRotation(previousRotation);
        }
    }

    /** Drops the current piece to the bottom and locks it. */
    private void hardDropPiece() {
        while (movePiece(0, 1)) {
        }
        lockPiece();
        fallAccumulator = 0;
    }

    /** Locks the current piece into the board, clears lines, and spawns next. */
    private void lockPiece() {
        int[][] cells = currentPiece.getCells();
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                if (cells[row][col] == 1) {
                    int boardX = pieceX + col;
                    int boardY = pieceY + row;
                    if (boardY >= 0 && boardY < ROWS && boardX >= 0 && boardX < COLS) {
                        board[boardX][boardY] = currentPiece.getType() + 1;
                    }
                }
            }
        }

        clearLines();
        if (flashingRows.isEmpty()) {
            // No lines to clear, spawn immediately
            if (!spawnNextPiece()) {
                gameOver = true;
                saveHighScore();
                sound.playGameOver();
            }
        }
        // If flash is active, spawn will happen after flash completes in update()
    }

    /** Clears full rows from bottom to top and updates score. */
    private void clearLines() {
        for (int row = ROWS - 1; row >= 0; row--) {
            boolean full = true;
            for (int col = 0; col < COLS; col++) {
                if (board[col][row] == 0) {
                    full = false;
                    break;
                }
            }
            if (full) {
                flashingRows.add(row);
            }
        }
        if (!flashingRows.isEmpty()) {
            flashCounter = 0;
        }
    }

    /** Actually clears the flashing rows and updates score. */
    private void performLineClear() {
        int cleared = flashingRows.size();
        if (cleared > 0) {
            Set<Integer> clearedRows = new HashSet<>(flashingRows);
            int writeRow = ROWS - 1;
            for (int readRow = ROWS - 1; readRow >= 0; readRow--) {
                if (clearedRows.contains(readRow)) {
                    continue;
                }
                for (int col = 0; col < COLS; col++) {
                    board[col][writeRow] = board[col][readRow];
                }
                writeRow--;
            }
            while (writeRow >= 0) {
                for (int col = 0; col < COLS; col++) {
                    board[col][writeRow] = 0;
                }
                writeRow--;
            }
            lines += cleared;
            score += scoringStrategy.calculate(cleared, level);
            level = 1 + lines / 10;
            sound.playLineClear();
            if (score > highScore) {
                highScore = score;
                saveHighScore();
            }
            notifyScoreListeners();
        }
    }

    /** Moves the next piece into play and prepares a new preview piece. */
    private boolean spawnNextPiece() {
        currentPiece = nextPiece;
        nextPiece = TetrominoFactory.createRandom(random);
        pieceX = 3;
        pieceY = 0;
        return canPlace(currentPiece, pieceX, pieceY, currentPiece.getRotation());
    }

    /** Checks whether a piece fits at a given position and rotation. */
    private boolean canPlace(Tetromino piece, int x, int y, int rotation) {
        int[][] cells = piece.getCells(rotation);
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                if (cells[row][col] == 1) {
                    int boardX = x + col;
                    int boardY = y + row;
                    if (boardX < 0 || boardX >= COLS || boardY >= ROWS) {
                        return false;
                    }
                    if (boardY >= 0 && board[boardX][boardY] != 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /** Returns the gravity interval in milliseconds. */
    private long getGravityInterval() {
        if (practiceMode) {
            return BASE_INTERVAL;
        }
        return Math.max(100L, BASE_INTERVAL - (level - 1L) * 40L);
    }

    /** Returns the current score. */
    public int getScore() {
        return score;
    }

    /** Returns the saved high score. */
    public int getHighScore() {
        return highScore;
    }

    /** Returns the current level. */
    public int getLevel() {
        return level;
    }

    /** Returns the total cleared lines. */
    public int getLines() {
        return lines;
    }

    /** Returns true when practice mode is active. */
    public boolean isPracticeMode() {
        return practiceMode;
    }

    /** Returns true when the game has ended. */
    public boolean isGameOver() {
        return gameOver;
    }

    /** Returns the next preview piece. */
    public Tetromino getNextPiece() {
        return nextPiece;
    }

    /** Draws the board, the falling piece, and the HUD. */
    public void draw(Graphics2D g) {
        g.setColor(new Color(18, 18, 22));
        g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);

        drawBoard(g);
        drawGhostPiece(g);
        drawPiece(g, currentPiece, pieceX, pieceY, currentPiece.getColor(), 255);
        drawHud(g);

        if (gameOver) {
            drawGameOver(g);
        }
    }

    /** Draws the playfield background and locked cells. */
    private void drawBoard(Graphics2D g) {
        g.setColor(new Color(10, 12, 18));
        g.fillRoundRect(BOARD_X - 4, BOARD_Y - 4, COLS * CELL + 8, ROWS * CELL + 8, 14, 14);
        g.setColor(new Color(44, 48, 62));
        g.drawRoundRect(BOARD_X - 4, BOARD_Y - 4, COLS * CELL + 8, ROWS * CELL + 8, 14, 14);

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                int value = board[col][row];
                if (value != 0) {
                    Color cellColor = colors[value - 1];
                    if (flashingRows.contains(row)) {
                        cellColor = Color.WHITE;
                    }
                    drawCell(g, BOARD_X + col * CELL, BOARD_Y + row * CELL, cellColor, 255);
                }
            }
        }
        
        g.setColor(new Color(100, 110, 130, 80));
        for (int i = 1; i < ROWS; i++) {
            g.drawLine(BOARD_X, BOARD_Y + i * CELL, BOARD_X + COLS * CELL, BOARD_Y + i * CELL);
        }
        for (int i = 1; i < COLS; i++) {
            g.drawLine(BOARD_X + i * CELL, BOARD_Y, BOARD_X + i * CELL, BOARD_Y + ROWS * CELL);
        }
    }

    /** Draws the ghost piece at the lowest valid position. */
    private void drawGhostPiece(Graphics2D g) {
        int ghostY = pieceY;
        while (canPlace(currentPiece, pieceX, ghostY + 1, currentPiece.getRotation())) {
            ghostY++;
        }
        drawPiece(g, currentPiece, pieceX, ghostY, Color.GRAY, 90);
    }

    /** Draws one tetromino using board coordinates. */
    private void drawPiece(Graphics2D g, Tetromino piece, int x, int y, Color color, int alpha) {
        int[][] cells = piece.getCells();
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                if (cells[row][col] == 1) {
                    int cellX = BOARD_X + (x + col) * CELL;
                    int cellY = BOARD_Y + (y + row) * CELL;
                    drawCell(g, cellX, cellY, color, alpha);
                }
            }
        }
    }

    /** Draws one colored square with a small gap around it. */
    private void drawCell(Graphics2D g, int x, int y, Color color, int alpha) {
        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
        g.fillRect(x + 1, y + 1, CELL - 2, CELL - 2);
    }

    /** Draws the score, level, lines, and next-piece preview. */
    private void drawHud(Graphics2D g) {
        g.setColor(new Color(235, 235, 240));
        g.setFont(new Font("SansSerif", Font.BOLD, 18));
        g.drawString("Score", HUD_X, 120);
        g.drawString(String.valueOf(score), HUD_X, 145);
        g.drawString("Best", HUD_X, 170);
        g.drawString(String.valueOf(highScore), HUD_X, 195);
        g.drawString("Level", HUD_X, 245);
        g.drawString(String.valueOf(level), HUD_X, 270);
        g.drawString("Lines", HUD_X, 320);
        g.drawString(String.valueOf(lines), HUD_X, 345);
        g.drawString(practiceMode ? "Practice" : "Marathon", HUD_X, 395);

        g.drawString("Next", HUD_X, 455);
        int[][] cells = nextPiece.getCells(0);
        int previewX = HUD_X;
        int previewY = 480;
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                if (cells[row][col] == 1) {
                    g.setColor(nextPiece.getColor());
                    g.fillRect(previewX + col * 22, previewY + row * 22, 21, 21);
                }
            }
        }
    }

    /** Draws the dark overlay and restart message. */
    private void drawGameOver(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 160));
        g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 44));
        FontMetrics metrics = g.getFontMetrics();
        String title = "GAME OVER";
        g.drawString(title, (GamePanel.WIDTH - metrics.stringWidth(title)) / 2, GamePanel.HEIGHT / 2 - 10);
        g.setFont(new Font("SansSerif", Font.PLAIN, 20));
        String hint = "Press R to restart";
        g.drawString(hint, (GamePanel.WIDTH - g.getFontMetrics().stringWidth(hint)) / 2, GamePanel.HEIGHT / 2 + 24);
        g.setFont(new Font("SansSerif", Font.PLAIN, 18));
        String best = "Best: " + highScore;
        g.drawString(best, (GamePanel.WIDTH - g.getFontMetrics().stringWidth(best)) / 2, GamePanel.HEIGHT / 2 + 52);
    }

    /** Loads the high score from disk, or returns zero if the file is missing. */
    private int loadHighScore() {
        try {
            if (!Files.exists(HIGH_SCORE_FILE)) {
                return 0;
            }
            String text = Files.readString(HIGH_SCORE_FILE, StandardCharsets.UTF_8).trim();
            return text.isEmpty() ? 0 : Integer.parseInt(text);
        } catch (IOException | NumberFormatException exception) {
            return 0;
        }
    }

    /** Saves the current high score to disk. */
    private void saveHighScore() {
        try {
            Files.writeString(HIGH_SCORE_FILE, String.valueOf(highScore), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            System.err.println("Could not save high score: " + exception.getMessage());
        }
        notifyScoreListeners();
    }

    /** Notifies all score listeners with the latest values. */
    private void notifyScoreListeners() {
        for (ScoreListener listener : scoreListeners) {
            listener.onScoreChanged(score, level, lines, highScore);
        }
    }
}