package mino;
import java.awt.Color;
import MainMethods.GameManager;

public class Mino_I extends Mino {
    // 
    // o o o o
    // 
    public Mino_I(GameManager gm){
        super(gm);
        create(Color.cyan);
    }

    @Override
    protected int srsPieceKind() {
        return 1;
    }

    @Override
    public void setXY(int x, int y){
        b[0].x = x;
        b[0].y = y;
        b[1].x = b[0].x - Block.SIZE;
        b[1].y = b[0].y;
        b[2].x = b[0].x + Block.SIZE;
        b[2].y = b[0].y;
        b[3].x = b[0].x + Block.SIZE * 2;
        b[3].y = b[0].y;
    }
    @Override
    public void getDirection1 () {
        // 
        // o o o o
        // 
        tempB[0].x = b[0].x;
        tempB[0].y = b[0].y;
        tempB[1].x = b[0].x - Block.SIZE;
        tempB[1].y = b[0].y;
        tempB[2].x = b[0].x + Block.SIZE;
        tempB[2].y = b[0].y;
        tempB[3].x = b[0].x + Block.SIZE * 2;
        tempB[3].y = b[0].y;

        updateXY(1);
    }
    @Override
    public void getDirection2 () {
        // o
        // o
        // o
        // o
        tempB[0].x = b[0].x;
        tempB[0].y = b[0].y;
        tempB[1].x = b[0].x;
        tempB[1].y = b[0].y - Block.SIZE;
        tempB[2].x = b[0].x;
        tempB[2].y = b[0].y + Block.SIZE;
        tempB[3].x = b[0].x;
        tempB[3].y = b[0].y + Block.SIZE * 2;
        
        updateXY(2);
    }
    @Override
    public void getDirection3() {
        // horizontal (same shape as state 1; direction index must be 3 for SRS)
        tempB[0].x = b[0].x;
        tempB[0].y = b[0].y;
        tempB[1].x = b[0].x - Block.SIZE;
        tempB[1].y = b[0].y;
        tempB[2].x = b[0].x + Block.SIZE;
        tempB[2].y = b[0].y;
        tempB[3].x = b[0].x + Block.SIZE * 2;
        tempB[3].y = b[0].y;
        updateXY(3);
    }
    @Override
    public void getDirection4() {
        // vertical (same shape as state 2)
        tempB[0].x = b[0].x;
        tempB[0].y = b[0].y;
        tempB[1].x = b[0].x;
        tempB[1].y = b[0].y - Block.SIZE;
        tempB[2].x = b[0].x;
        tempB[2].y = b[0].y + Block.SIZE;
        tempB[3].x = b[0].x;
        tempB[3].y = b[0].y + Block.SIZE * 2;
        updateXY(4);
    }
}
