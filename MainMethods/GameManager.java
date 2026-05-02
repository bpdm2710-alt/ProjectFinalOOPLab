package MainMethods;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

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
    public static ArrayList<Block> staticBlocks = new ArrayList<>();

    public static int dropInterval = 60;

    public GameManager(){
        left_x = (GamePanel.WIDTH - WIDTH) / 2;
        right_x = left_x + WIDTH;
        top_y = 50;
        bottom_y = top_y + HEIGHT;

        MINO_START_X = left_x + WIDTH / 2 - Block.SIZE;
        MINO_START_Y = top_y + Block.SIZE;
        NEXTMINO_X = right_x + 175;
        NEXTMINO_Y = top_y + 200;

        currentMino = getRandomMino();
        currentMino.setXY(MINO_START_X, MINO_START_Y);
        nextMino = getRandomMino();
        nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);
    }
    private Mino getRandomMino(){
        Mino mino = null;
        int i = new Random().nextInt(7);
        switch(i){
            case 0: mino = new Mino_L(); break;
            case 1: mino = new Mino_J(); break;
            case 2: mino = new Mino_I(); break;
            case 3: mino = new Mino_O(); break;
            case 4: mino = new Mino_Z(); break;
            case 5: mino = new Mino_T(); break;
            case 6: mino = new Mino_S(); break;
        }
        return mino;
    }
    public void update (){
        if (currentMino.activeMino == false){
            staticBlocks.add(currentMino.b[0]);
            staticBlocks.add(currentMino.b[1]);
            staticBlocks.add(currentMino.b[2]);
            staticBlocks.add(currentMino.b[3]);

            currentMino.deactivating = false;

            currentMino = nextMino;
            currentMino.setXY(MINO_START_X, MINO_START_Y);
            nextMino = getRandomMino();
            nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);
        }else {
            currentMino.update();
        }
    }

    public void draw(Graphics2D g2){
        //Main area
        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(4f));
        g2.drawRect(left_x-8, top_y-8, WIDTH+16, HEIGHT+16);

        // preview area
        int x = right_x + 100;
        int y = bottom_y - 550;
        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(4f));
        g2.drawRect(x, y, 200, 500);
        g2.setFont(new Font("Arial", Font.PLAIN, 20));
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.drawString("Preview", x + 70, y + 30);

        //draw current mino
        if(currentMino != null){
            currentMino.draw(g2);
        }

        //draw next mino
        nextMino.draw(g2);

        //draw static blocks
        for(int i = 0; i < staticBlocks.size(); i++){
            staticBlocks.get(i).draw(g2);
        }
        
        //draw pause
        g2.setColor(Color.yellow);
        g2.setFont(g2.getFont().deriveFont(50f));
        if (KeyHandler.PausedGame){
            g2.drawString("PAUSED", GamePanel.WIDTH / 2 - 100, GamePanel.HEIGHT / 2);
            g2.drawString("Press P again", GamePanel.WIDTH / 2 - 150, GamePanel.HEIGHT / 2 + 60);
        }
    }

}
