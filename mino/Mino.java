package mino;

import java.awt.Color;
import java.awt.Graphics2D;
import MainMethods.GameManager;
import MainMethods.GamePanel;
import MainMethods.KeyHandler;

public class Mino {
    /** Lock delay at 60 FPS ≈ 0.75 s (Tetris Guideline-style). */
    public static final int LOCK_DELAY_FRAMES = 45;

    public static volatile int DAS_DELAY = 10;
    public static volatile int ARR_DELAY = 2;
    public static volatile int SDF_MULTIPLIER = 20;
    public static final int MAX_LOCK_RESETS = 15;

    protected int dasLeftCounter = 0;
    protected int dasRightCounter = 0;
    protected int lockResets = 0;

    protected GameManager gm;

    public Mino(GameManager gm) {
        this.gm = gm;
    }

    public Block b[] = new Block[4];
    public Block tempB[] = new Block[4];
    boolean leftCollision = false;
    boolean rightCollision = false;
    boolean downCollision = false;
    public boolean activeMino = true;
    public boolean deactivating;
    int deactivateCounter = 0;

    int autoDropCounter = 0;
    public int direction = 1; // 4 directions
    protected boolean lastRotationUsedKick;
    protected boolean doing180 = false;
    protected boolean wasDownCollision = false;

    /** SRS: 0 = O, 1 = I, 2 = JLSTZ */
    protected int srsPieceKind() {
        return 2;
    }

    public void create(Color c) {
        b[0] = new Block(c);
        b[1] = new Block(c);
        b[2] = new Block(c);
        b[3] = new Block(c);
        tempB[0] = new Block(c);
        tempB[1] = new Block(c);
        tempB[2] = new Block(c);
        tempB[3] = new Block(c);
    }

    public void setXY(int x, int y) {
    }

    public boolean updateXY(int newDirection) {
        if (doing180) {
            if (canPlaceTempBlocks(0, 0)) {
                applyTempBlocks(newDirection, 0, 0);
                lastRotationUsedKick = false;
                return true;
            }
            return false;
        }

        int oldState = direction - 1;
        int newState = newDirection - 1;

        int[][] kicks;
        if (srsPieceKind() == 0) {
            kicks = new int[][] { { 0, 0 } };
        } else if (srsPieceKind() == 1) {
            kicks = SRSKickTable.getIKicks(oldState, newState);
        } else {
            kicks = SRSKickTable.getJLSTZKicks(oldState, newState);
        }

        for (int i = 0; i < kicks.length; i++) {
            if (canPlaceTempBlocks(kicks[i][0], kicks[i][1])) {
                applyTempBlocks(newDirection, kicks[i][0], kicks[i][1]);
                lastRotationUsedKick = kicks[i][0] != 0 || kicks[i][1] != 0;
                return true;
            }
        }

        lastRotationUsedKick = false;
        return false;
    }

    public void getDirection1() {
    }

    public void getDirection2() {
    }

    public void getDirection3() {
    }

    public void getDirection4() {
    }



    public void checkMovementCollision() {
        rightCollision = false;
        leftCollision = false;
        downCollision = false;
        checkStaticBlockCollision();
        for (int i = 0; i < b.length; i++) {
            if (b[i].x == gm.getLeftX()) {
                leftCollision = true;
            }
        }
        for (int i = 0; i < b.length; i++) {
            if (b[i].x + Block.SIZE == gm.getRightX()) {
                rightCollision = true;
            }
        }
        for (int i = 0; i < b.length; i++) {
            if (b[i].y + Block.SIZE == gm.getBottomY()) {
                downCollision = true;
            }
        }
    }

    public void checkStaticBlockCollision() {
        java.util.List<Block> staticBlocks = gm.getStaticBlocks();
        for (int i = 0; i < staticBlocks.size(); i++) {
            int targetX = staticBlocks.get(i).x;
            int targetY = staticBlocks.get(i).y;

            for (int j = 0; j < b.length; j++) {
                if (b[j].x == targetX && b[j].y + Block.SIZE == targetY) {
                    downCollision = true;
                }
                if (b[j].x + Block.SIZE == targetX && b[j].y == targetY) {
                    rightCollision = true;
                }
                if (b[j].x - Block.SIZE == targetX && b[j].y == targetY) {
                    leftCollision = true;
                }
            }
        }
    }

    public void update() {

        if (deactivating) {
            deactivating();
        }
        checkMovementCollision();

        boolean manipulated = false;

        if (KeyHandler.isLeftPressed()) {
            if (dasLeftCounter == 0 || (dasLeftCounter >= DAS_DELAY
                    && (ARR_DELAY == 0 || (dasLeftCounter - DAS_DELAY) % ARR_DELAY == 0))) {
                if (ARR_DELAY == 0 && dasLeftCounter >= DAS_DELAY) {
                    while (!leftCollision) {
                        b[0].x -= Block.SIZE;
                        b[1].x -= Block.SIZE;
                        b[2].x -= Block.SIZE;
                        b[3].x -= Block.SIZE;
                        checkMovementCollision();
                        manipulated = true;
                    }
                } else if (!leftCollision) {
                    b[0].x -= Block.SIZE;
                    b[1].x -= Block.SIZE;
                    b[2].x -= Block.SIZE;
                    b[3].x -= Block.SIZE;
                    checkMovementCollision();
                    manipulated = true;
                }
            }
            dasLeftCounter++;
        } else {
            dasLeftCounter = 0;
        }

        if (KeyHandler.isRightPressed()) {
            if (dasRightCounter == 0 || (dasRightCounter >= DAS_DELAY
                    && (ARR_DELAY == 0 || (dasRightCounter - DAS_DELAY) % ARR_DELAY == 0))) {
                if (ARR_DELAY == 0 && dasRightCounter >= DAS_DELAY) {
                    while (!rightCollision) {
                        b[0].x += Block.SIZE;
                        b[1].x += Block.SIZE;
                        b[2].x += Block.SIZE;
                        b[3].x += Block.SIZE;
                        checkMovementCollision();
                        manipulated = true;
                    }
                } else if (!rightCollision) {
                    b[0].x += Block.SIZE;
                    b[1].x += Block.SIZE;
                    b[2].x += Block.SIZE;
                    b[3].x += Block.SIZE;
                    checkMovementCollision();
                    manipulated = true;
                }
            }
            dasRightCounter++;
        } else {
            dasRightCounter = 0;
        }

        if (KeyHandler.isDownPressed()) {
            int dropDistance = 0;
            while (!downCollision && dropDistance < SDF_MULTIPLIER) {
                b[0].y += Block.SIZE;
                b[1].y += Block.SIZE;
                b[2].y += Block.SIZE;
                b[3].y += Block.SIZE;
                dropDistance++;
                checkMovementCollision();
            }
            if (dropDistance > 0) {
                gm.addScore(dropDistance);
                autoDropCounter = 0;
            }
        } else {
            // not holding down — nothing to reset; keyReleased already cleared the flag
        }

        boolean rotated = false;
        if (KeyHandler.consumeRotateClockwise()) {
            if (rotateClockwise()) {
                GamePanel.getEffect().playEffect(3);
                rotated = true;
            }
        }
        if (KeyHandler.consumeRotateCounterClockwise()) {
            if (rotateCounterClockwise()) {
                GamePanel.getEffect().playEffect(3);
                rotated = true;
            }
        }
        if (KeyHandler.consumeRotateHalfTurn()) {
            if (rotateHalfTurn()) {
                GamePanel.getEffect().playEffect(3);
                rotated = true;
            }
        }

        if (rotated) {
            checkMovementCollision();
            manipulated = true;
        }

        if (manipulated && downCollision) {
            if (lockResets < MAX_LOCK_RESETS) {
                deactivateCounter = 0;
                lockResets++;
            }
        }

        if (downCollision) {
            if (deactivating == false && !wasDownCollision) {
                GamePanel.getEffect().playEffect(4);
            }
            deactivating = true;
            wasDownCollision = true;
        } else {
            deactivating = false;
            deactivateCounter = 0;
            autoDropCounter++;
            if (autoDropCounter == gm.getDropInterval()) {
                b[0].y += Block.SIZE;
                b[1].y += Block.SIZE;
                b[2].y += Block.SIZE;
                b[3].y += Block.SIZE;
                autoDropCounter = 0;
            }
            wasDownCollision = false;
        }
    }

    public void deactivating() {
        deactivateCounter++;
        if (deactivateCounter >= LOCK_DELAY_FRAMES) {
            checkMovementCollision();
            if (downCollision) {
                activeMino = false;
            }
        }
    }

    public void draw(Graphics2D g2) {
        for (Block block : b) {
            block.draw(g2);
        }
    }

    public boolean rotateClockwise() {
        int oldDirection = direction;
        switch (direction) {
            case 1:
                getDirection2();
                break;
            case 2:
                getDirection3();
                break;
            case 3:
                getDirection4();
                break;
            case 4:
                getDirection1();
                break;
        }
        return direction != oldDirection;
    }

    public boolean rotateCounterClockwise() {
        int oldDirection = direction;
        switch (direction) {
            case 1:
                getDirection4();
                break;
            case 2:
                getDirection1();
                break;
            case 3:
                getDirection2();
                break;
            case 4:
                getDirection3();
                break;
        }
        return direction != oldDirection;
    }

    public boolean rotateHalfTurn() {
        int oldDirection = direction;
        doing180 = true;

        switch (direction) {
            case 1:
                getDirection3();
                break;
            case 2:
                getDirection4();
                break;
            case 3:
                getDirection1();
                break;
            case 4:
                getDirection2();
                break;
        }

        doing180 = false;
        return direction != oldDirection;
    }

    private boolean canPlaceTempBlocks(int offsetX, int offsetY) {
        boolean left = false;
        boolean right = false;
        boolean down = false;

        for (int i = 0; i < b.length; i++) {
            int nextX = tempB[i].x + offsetX * Block.SIZE;
            int nextY = tempB[i].y + offsetY * Block.SIZE;

            // Check boundary collisions
            if (nextX < gm.getLeftX()) {
                left = true;
            }
            if (nextX + Block.SIZE > gm.getRightX()) {
                right = true;
            }
            if (nextY + Block.SIZE > gm.getBottomY()) {
                down = true;
            }

            // Check static block collisions
            java.util.List<Block> staticBlocks = gm.getStaticBlocks();
            for (int j = 0; j < staticBlocks.size(); j++) {
                Block staticBlock = staticBlocks.get(j);
                if (nextX == staticBlock.x && nextY == staticBlock.y) {
                    down = true;
                }
            }
        }

        return !left && !right && !down;
    }

    private void applyTempBlocks(int newDirection, int offsetX, int offsetY) {
        direction = newDirection;
        for (int i = 0; i < b.length; i++) {
            b[i].x = tempB[i].x + offsetX * Block.SIZE;
            b[i].y = tempB[i].y + offsetY * Block.SIZE;
        }
    }
}
