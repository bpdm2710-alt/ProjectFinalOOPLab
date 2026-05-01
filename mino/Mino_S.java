package mino;
import java.awt.Color;

public class Mino_S extends Mino {
    //  o o
    //o o
    public Mino_S(){
        create(Color.GREEN);
    }
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
    public void getDirection1() {
        //  o o
        //o o
        tempB[0].x = b[0].x;
        tempB[0].y = b[0].y;
        tempB[1].x = b[0].x + Block.SIZE;
        tempB[1].y = b[0].y;
        tempB[2].x = b[0].x - Block.SIZE;
        tempB[2].y = b[0].y + Block.SIZE;
        tempB[3].x = b[0].x;
        tempB[3].y = b[0].y + Block.SIZE;

        updateXY(1);
    }
    public void getDirection2() {
        // o
        // o o
        //   o
        tempB[0].x = b[0].x;
        tempB[0].y = b[0].y;
        tempB[1].x = b[0].x;
        tempB[1].y = b[0].y - Block.SIZE;
        tempB[2].x = b[0].x + Block.SIZE;
        tempB[2].y = b[0].y;
        tempB[3].x = b[0].x + Block.SIZE;
        tempB[3].y = b[0].y + Block.SIZE;

        updateXY(2);
    }
    public void getDirection3() {
        //  o o
        //o o
        getDirection1();
    }
    public void getDirection4() {
        // o
        // o o
        //   o
        getDirection2();
    }
}
