package MainMethods;

import java.util.Random;

import mino.Mino;
import mino.Mino_I;
import mino.Mino_J;
import mino.Mino_L;
import mino.Mino_O;
import mino.Mino_S;
import mino.Mino_T;
import mino.Mino_Z;

public class MinoFactory {
    private static final Random RANDOM = new Random();

    private MinoFactory() {
    }

    public static int getRandomType() {
        return RANDOM.nextInt(7);
    }

    public static Mino createByType(int type) {
        switch (type) {
            case 0:
                return new Mino_L();
            case 1:
                return new Mino_J();
            case 2:
                return new Mino_I();
            case 3:
                return new Mino_O();
            case 4:
                return new Mino_Z();
            case 5:
                return new Mino_T();
            case 6:
                return new Mino_S();
            default:
                return new Mino_O();
        }
    }
}
