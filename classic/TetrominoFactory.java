package classic;

import java.util.Random;

/** Small factory used to create random pieces. */
public final class TetrominoFactory {
    private TetrominoFactory() {
    }

    /** Creates a random tetromino. */
    public static Tetromino createRandom(Random random) {
        return new Tetromino(random.nextInt(7));
    }
}