package MainMethods;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    private static ArrayList<Integer> currentBag = new ArrayList<>();
    private static int bagIndex = 7;

    private MinoFactory() {
    }

    /**
     * Reset the 7-bag randomizer state.
     * Must be called when game restarts to ensure piece sequence resets.
     */
    public static void resetBag() {
        currentBag.clear();
        bagIndex = 7;
    }

    public static int getRandomType() {
        if (bagIndex >= currentBag.size()) {
            currentBag = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6));
            Collections.shuffle(currentBag, RANDOM);
            bagIndex = 0;
        }
        return currentBag.get(bagIndex++);
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
