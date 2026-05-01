package mino;
import java.awt.Color;

public class MinoBar extends Mino {
    // o
    // o 
    // o
    // o
    public MinoBar(){
        create(Color.cyan);
    }
    public void setXY(int x, int y){
        b[0].x = x;
        b[0].y = y;
        b[1].x = b[0].x;
        b[1].y = b[0].y + Block.SIZE;
        b[2].x = b[0].x;
        b[2].y = b[0].y + 2 * Block.SIZE;
        b[3].x = b[0].x;
        b[3].y = b[0].y + 3 * Block.SIZE;
    }

}
