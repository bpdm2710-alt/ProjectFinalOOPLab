import java.util.ArrayList;
import java.util.List;

class Block {
    int x, y;
    public Block(int x, int y) { this.x = x; this.y = y; }
    public String toString() { return "(" + x + "," + y + ")"; }
}

public class TestClear {
    public static void main(String[] args) {
        ArrayList<Block> staticBlocks = new ArrayList<>();
        int top_y = 0;
        int bottom_y = 400;
        int Block_SIZE = 10;
        
        // Add full row at 200
        for(int x=0; x<10; x++) staticBlocks.add(new Block(x*10, 200));
        // Add full row at 210
        for(int x=0; x<10; x++) staticBlocks.add(new Block(x*10, 210));
        
        // Add some blocks above
        staticBlocks.add(new Block(0, 190));
        staticBlocks.add(new Block(0, 180));
        
        int y = top_y;
        int lineCount = 0;

        while (y < bottom_y) {
            int blockCount = 0;

            for (int i = 0; i < staticBlocks.size(); i++) {
                if (staticBlocks.get(i).y == y) {
                    blockCount++;
                }
            }

            if (blockCount == 10) {
                System.out.println("Clearing row " + y);
                for (int i = staticBlocks.size() - 1; i > -1; i--) {
                    if (staticBlocks.get(i).y == y) {
                        staticBlocks.remove(i);
                    }
                }
                lineCount++;

                for (int i = 0; i < staticBlocks.size(); i++) {
                    if (staticBlocks.get(i).y < y) {
                        staticBlocks.get(i).y += Block_SIZE;
                    }
                }
            } else {
                y += Block_SIZE;
            }
        }
        
        System.out.println("Remaining blocks:");
        for(Block b : staticBlocks) {
            System.out.println(b);
        }
    }
}
