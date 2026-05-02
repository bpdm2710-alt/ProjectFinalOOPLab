package mino;

import java.awt.Color;
import java.awt.Graphics2D;
import MainMethods.GameManager;
import MainMethods.GamePanel;
import MainMethods.KeyHandler;

public class Mino {
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


    public void create (Color c){
        b[0] = new Block(c);
        b[1] = new Block(c);
        b[2] = new Block(c);
        b[3] = new Block(c);
        tempB[0] = new Block(c);
        tempB[1] = new Block(c);
        tempB[2] = new Block(c);
        tempB[3] = new Block(c);
    }
    public void setXY(int x, int y){}
    public void updateXY(int direction){
        checkRotationCollision();
        if (!leftCollision && !rightCollision && !downCollision){
            this.direction = direction;
            b[0].x = tempB[0].x;
            b[0].y = tempB[0].y;
            b[1].x = tempB[1].x;
            b[1].y = tempB[1].y;
            b[2].x = tempB[2].x;
            b[2].y = tempB[2].y;
            b[3].x = tempB[3].x;
            b[3].y = tempB[3].y;
        }
    }
    public void getDirection1 () {}
    public void getDirection2 () {}
    public void getDirection3 () {}
    public void getDirection4 () {}
    public void checkRotationCollision () {
        rightCollision = false;
        leftCollision = false;
        downCollision = false;
        checkStaticBlockCollision();

        for (int i = 0; i < b.length; i++){
            if (tempB[i].x < GameManager.left_x){
                leftCollision = true;
            }
        }
        for (int i = 0; i < b.length; i++){
            if (tempB[i].x + Block.SIZE > GameManager.right_x){
                rightCollision = true;
            }
        }
        for (int i = 0; i < b.length; i++){
            if (tempB[i].y + Block.SIZE > GameManager.bottom_y){
                downCollision = true;
            }
        }
    }
    public void checkMovementCollision () {
        rightCollision = false;
        leftCollision = false;
        downCollision = false;
        checkStaticBlockCollision();
        for (int i = 0; i < b.length; i++){
            if (b[i].x == GameManager.left_x){
                leftCollision = true;
            }
        }
        for (int i = 0; i < b.length; i++){
            if (b[i].x + Block.SIZE == GameManager.right_x){
                rightCollision = true;
            }
        }
        for (int i = 0; i < b.length; i++){
            if (b[i].y + Block.SIZE == GameManager.bottom_y){
                downCollision = true;
            }
        }
    }
    public void checkStaticBlockCollision(){
        for (int i = 0; i < GameManager.staticBlocks.size(); i++){
            int TargetX = GameManager.staticBlocks.get(i).x;
            int TargetY = GameManager.staticBlocks.get(i).y;

            for (int j = 0; j < b.length; j++){
                if (b[j].x == TargetX && b[j].y + Block.SIZE == TargetY){
                    downCollision = true;
                }
                if (b[j].x + Block.SIZE == TargetX && b[j].y == TargetY){
                    rightCollision = true;
                }
                if (b[j].x - Block.SIZE == TargetX && b[j].y == TargetY){
                    leftCollision = true;
                }
            }
        }
    }
    public void update (){

        if (deactivating){
            deactivating();
        }
        checkMovementCollision();

        if (KeyHandler.leftPressed){
            if (!leftCollision){
                b[0].x -= Block.SIZE;
                b[1].x -= Block.SIZE;
                b[2].x -= Block.SIZE;
                b[3].x -= Block.SIZE;
            autoDropCounter = 0;
            }
            KeyHandler.leftPressed = false;
        }
        if(KeyHandler.rightPressed){
            if (!rightCollision){
                b[0].x += Block.SIZE;
                b[1].x += Block.SIZE;
                b[2].x += Block.SIZE;
                b[3].x += Block.SIZE;
                autoDropCounter = 0;
            }
            KeyHandler.rightPressed = false;
        }
        if(KeyHandler.downPressed){
            if (!downCollision){
                b[0].y += Block.SIZE;
                b[1].y += Block.SIZE;
                b[2].y += Block.SIZE;
                b[3].y += Block.SIZE;
                autoDropCounter = 0;
            }
            KeyHandler.downPressed = false;
        }
        if (KeyHandler.UpPressed) {
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
            KeyHandler.UpPressed = false;
            GamePanel.effect.play(2, false);
        }

        // Re-check after movement so lock logic uses the current position.
        checkMovementCollision();

        if(downCollision){
            if (deactivating == false){
                GamePanel.effect.play(3, false);
            }
            deactivating = true;
        }
        else {
            deactivating = false;
            deactivateCounter = 0;
            autoDropCounter++;
            if (autoDropCounter == GameManager.dropInterval){
            b[0].y += Block.SIZE;
            b[1].y += Block.SIZE;
            b[2].y += Block.SIZE;
            b[3].y += Block.SIZE;
            autoDropCounter = 0;
            }
        }
    }
    public void deactivating(){
        deactivateCounter++;
        if (deactivateCounter >= 45){
            checkMovementCollision();
            if(downCollision){
                activeMino = false;
            }
        }
    }
    public void draw (Graphics2D g2){
        int margin = 2;
        g2.setColor(b[0].c);
        g2.fillRect(b[0].x + margin, b[0].y + margin, Block.SIZE - 2 * margin, Block.SIZE - 2 * margin);
        g2.fillRect(b[1].x + margin, b[1].y + margin, Block.SIZE - 2 * margin, Block.SIZE - 2 * margin);
        g2.fillRect(b[2].x + margin, b[2].y + margin, Block.SIZE - 2 * margin, Block.SIZE - 2 * margin);
        g2.fillRect(b[3].x + margin, b[3].y + margin, Block.SIZE - 2 * margin, Block.SIZE - 2 * margin);
    }
}

