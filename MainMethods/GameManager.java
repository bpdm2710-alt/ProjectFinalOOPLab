package MainMethods;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.Random;

/** Stores the board, the falling piece, scoring, and rendering. */
public class GameManager {
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

    private Tetromino currentPiece;
    private Tetromino nextPiece;
    private int pieceX;
    private int pieceY;
    private int score;
    private int level;
    private int lines;
    private boolean practiceMode;
    private boolean gameOver;
    private long fallAccumulator;
    private long lastUpdateTime;

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
        lastUpdateTime = System.currentTimeMillis();
        for (int col = 0; col < COLS; col++) {
            for (int row = 0; row < ROWS; row++) {
                board[col][row] = 0;
            }
        }
        currentPiece = TetrominoFactory.createRandom(random);
        nextPiece = TetrominoFactory.createRandom(random);
        pieceX = 3;
        pieceY = 0;
    }

    /** Updates input, gravity, and piece locking. */
    public void update() {
        if (gameOver) {
            return;
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
        if (!spawnNextPiece()) {
            gameOver = true;
            sound.playGameOver();
        }
    }

    /** Clears full rows from bottom to top and updates score. */
    private void clearLines() {
        int cleared = 0;
        for (int row = ROWS - 1; row >= 0; row--) {
            boolean full = true;
            for (int col = 0; col < COLS; col++) {
                if (board[col][row] == 0) {
                    full = false;
                    break;
                }
            }
            if (full) {
                cleared++;
                for (int pull = row; pull > 0; pull--) {
                    for (int col = 0; col < COLS; col++) {
                        board[col][pull] = board[col][pull - 1];
                    }
                }
                for (int col = 0; col < COLS; col++) {
                    board[col][0] = 0;
                }
                row++;
            }
        }

        if (cleared > 0) {
            lines += cleared;
            score += scoringStrategy.calculate(cleared, level);
            level = 1 + lines / 10;
            sound.playLineClear();
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
                    drawCell(g, BOARD_X + col * CELL, BOARD_Y + row * CELL, colors[value - 1], 255);
                }
            }
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
        g.drawString("Level", HUD_X, 195);
        g.drawString(String.valueOf(level), HUD_X, 220);
        g.drawString("Lines", HUD_X, 270);
        g.drawString(String.valueOf(lines), HUD_X, 295);
        g.drawString(practiceMode ? "Practice" : "Marathon", HUD_X, 345);

        g.drawString("Next", HUD_X, 405);
        int[][] cells = nextPiece.getCells(0);
        int previewX = HUD_X;
        int previewY = 430;
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
    }
}