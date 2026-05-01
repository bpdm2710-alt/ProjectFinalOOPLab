package mino;
import java.awt.Color;

public class MinoT extends Mino {
    // o o o
    //   o

    public MinoT(){
        create (Color.PINK);
    }
    public void setXY(int x, int y){
        b[0].x = x;
        b[0].y = y;
        b[1].x = b[0].x + Block.SIZE;
        b[1].y = b[0].y;
        b[2].x = b[0].x + Block.SIZE * 2;
        b[2].y = b[0].y;
        b[3].x = b[0].x + Block.SIZE;
        b[3].y = b[0].y + Block.SIZE;
    }
}
