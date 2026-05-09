package mino;
import java.awt.Color;
import MainMethods.GameManager;

public class Mino_Z extends Mino {
    // o o
    //   o o
    public Mino_Z(GameManager gm){
        super(gm);
        create(Color.RED);
    }
    @Override
    public void setXY(int x, int y){
        b[0].x = x;
        b[0].y = y;
        b[1].x = b[0].x + Block.SIZE;
        b[1].y = b[0].y;
        b[2].x = b[0].x;
        b[2].y = b[0].y - Block.SIZE;
        b[3].x = b[0].x - Block.SIZE;
        b[3].y = b[0].y - Block.SIZE;
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
        // For the Z-piece based on its initial setXY, b[0] is perfectly aligned 
        // with the standard SRS 3x3 pivot center for all rotations.
        int pivotX = b[0].x;
        int pivotY = b[0].y;

        if (targetDir == 1) {
            tempB[0].x = pivotX; tempB[0].y = pivotY;
            tempB[1].x = pivotX + Block.SIZE; tempB[1].y = pivotY;
            tempB[2].x = pivotX; tempB[2].y = pivotY - Block.SIZE;
            tempB[3].x = pivotX - Block.SIZE; tempB[3].y = pivotY - Block.SIZE;
        } else if (targetDir == 2) {
            tempB[0].x = pivotX; tempB[0].y = pivotY;
            tempB[1].x = pivotX + Block.SIZE; tempB[1].y = pivotY - Block.SIZE;
            tempB[2].x = pivotX + Block.SIZE; tempB[2].y = pivotY;
            tempB[3].x = pivotX; tempB[3].y = pivotY + Block.SIZE;
        } else if (targetDir == 3) {
            tempB[0].x = pivotX; tempB[0].y = pivotY;
            tempB[1].x = pivotX - Block.SIZE; tempB[1].y = pivotY;
            tempB[2].x = pivotX; tempB[2].y = pivotY + Block.SIZE;
            tempB[3].x = pivotX + Block.SIZE; tempB[3].y = pivotY + Block.SIZE;
        } else if (targetDir == 4) {
            tempB[0].x = pivotX; tempB[0].y = pivotY;
            tempB[1].x = pivotX; tempB[1].y = pivotY - Block.SIZE;
            tempB[2].x = pivotX - Block.SIZE; tempB[2].y = pivotY;
            tempB[3].x = pivotX - Block.SIZE; tempB[3].y = pivotY + Block.SIZE;
        }
        updateXY(targetDir);
    }
}
