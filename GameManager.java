import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class GameManager {
    final int WIDTH = 360;
    final int HEIGHT = 600;
    public static int left_x;
    public static int right_x;
    public static int top_y;
    public static int bottom_y;

    public GameManager(){
        left_x = (GamePanel.WIDTH - WIDTH) / 2;
        right_x = left_x + WIDTH;
        top_y = 50;
        bottom_y = top_y + HEIGHT;
    }
    public void update (){

    }

    public void draw(Graphics2D g2){
        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(4f));
        g2.drawRect(left_x-8, top_y-8, WIDTH+16, HEIGHT+16);

        int x = right_x + 100;
        int y = bottom_y - 100;
        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(4f));
    }

}
