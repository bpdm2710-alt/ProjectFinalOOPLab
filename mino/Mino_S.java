package mino;
import java.awt.Color;
import MainMethods.GameManager;

public class Mino_S extends Mino {
    //  o o
    //o o
    public Mino_S(GameManager gm){
        super(gm);
        create(Color.GREEN);
    }
    @Override
    public void setXY(int x, int y){
        b[0].x = x;
        b[0].y = y;
        b[1].x = x + Block.SIZE;
        b[1].y = y;
        b[2].x = x - Block.SIZE;
        b[2].y = y + Block.SIZE;
        b[3].x = x;
        b[3].y = y + Block.SIZE;
    }
    @Override
    public void getDirection1() {
        getDirection(1);
    }
    @Override
    public void getDirection2() {
        getDirection(2);
    }
    @Override
    public void getDirection3() {
        getDirection(3);
    }
    @Override
    public void getDirection4() {
        getDirection(4);
    }

    private void getDirection(int targetDir) {
        int pivotX = b[0].x;
        int pivotY = b[0].y;
        
        // b[0] is initialized at top-left of the S shape, not the true 3x3 center
        // Let's deduce the true 3x3 center based on current state.
        if (direction == 1) {
            pivotY += Block.SIZE;
        } else if (direction == 2) {
            pivotY += Block.SIZE;
        } else if (direction == 3) {
            // b[0] is exactly at the pivot in state 3
        } else if (direction == 4) {
            pivotX += Block.SIZE;
            pivotY += Block.SIZE;
        }

        if (targetDir == 1) {
            tempB[0].x = pivotX; tempB[0].y = pivotY - Block.SIZE;
            tempB[1].x = pivotX + Block.SIZE; tempB[1].y = pivotY - Block.SIZE;
            tempB[2].x = pivotX - Block.SIZE; tempB[2].y = pivotY;
            tempB[3].x = pivotX; tempB[3].y = pivotY;
        } else if (targetDir == 2) {
            tempB[0].x = pivotX; tempB[0].y = pivotY - Block.SIZE;
            tempB[1].x = pivotX; tempB[1].y = pivotY;
            tempB[2].x = pivotX + Block.SIZE; tempB[2].y = pivotY;
            tempB[3].x = pivotX + Block.SIZE; tempB[3].y = pivotY + Block.SIZE;
        } else if (targetDir == 3) {
            tempB[0].x = pivotX; tempB[0].y = pivotY;
            tempB[1].x = pivotX + Block.SIZE; tempB[1].y = pivotY;
            tempB[2].x = pivotX - Block.SIZE; tempB[2].y = pivotY + Block.SIZE;
            tempB[3].x = pivotX; tempB[3].y = pivotY + Block.SIZE;
        } else if (targetDir == 4) {
            tempB[0].x = pivotX - Block.SIZE; tempB[0].y = pivotY - Block.SIZE;
            tempB[1].x = pivotX - Block.SIZE; tempB[1].y = pivotY;
            tempB[2].x = pivotX; tempB[2].y = pivotY;
            tempB[3].x = pivotX; tempB[3].y = pivotY + Block.SIZE;
        }
        updateXY(targetDir);
    }
}
