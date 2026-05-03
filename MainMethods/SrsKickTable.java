package MainMethods;

import mino.Mino;
import mino.Mino_I;
import mino.Mino_O;

/**
 * Super Rotation System kick offsets (Tetris Guideline).
 * Cell offsets (dx, dy) with +y = downward on the playfield.
 * Tables from https://tetris.wiki/SRS
 */
public final class SrsKickTable {

    private SrsKickTable() {
    }

    /**
     * @param oldDir prior direction 1–4 (spawn=N=1, E=2, S=3, W=4)
     * @param newDir target direction after rotation
     */
    public static int[][] getKicks(Mino mino, int oldDir, int newDir) {
        if (mino instanceof Mino_O) {
            return KICK_O;
        }
        if (mino instanceof Mino_I) {
            return getIKicks(oldDir, newDir);
        }
        return getJlstzKicks(oldDir, newDir);
    }

    private static final int[][] KICK_O = {{0, 0}};

    private static int[][] getJlstzKicks(int oldDir, int newDir) {
        switch (oldDir * 10 + newDir) {
            case 12:
                return JLSTZ_0_TO_R;
            case 23:
                return JLSTZ_R_TO_2;
            case 34:
                return JLSTZ_2_TO_L;
            case 41:
                return JLSTZ_L_TO_0;
            case 21:
                return JLSTZ_R_TO_0;
            case 32:
                return JLSTZ_2_TO_R;
            case 43:
                return JLSTZ_L_TO_2;
            case 14:
                return JLSTZ_0_TO_L;
            default:
                return GENERIC_FALLBACK;
        }
    }

    private static int[][] getIKicks(int oldDir, int newDir) {
        switch (oldDir * 10 + newDir) {
            case 12:
                return I_0_TO_R;
            case 23:
                return I_R_TO_2;
            case 34:
                return I_2_TO_L;
            case 41:
                return I_L_TO_0;
            case 21:
                return I_R_TO_0;
            case 32:
                return I_2_TO_R;
            case 43:
                return I_L_TO_2;
            case 14:
                return I_0_TO_L;
            default:
                return GENERIC_FALLBACK;
        }
    }

    // JLSTZ — clockwise
    private static final int[][] JLSTZ_0_TO_R = {{0, 0}, {-1, 0}, {-1, 1}, {0, -2}, {-1, -2}};
    private static final int[][] JLSTZ_R_TO_2 = {{0, 0}, {1, 0}, {1, -1}, {0, 2}, {1, 2}};
    private static final int[][] JLSTZ_2_TO_L = {{0, 0}, {1, 0}, {1, 1}, {0, -2}, {1, -2}};
    private static final int[][] JLSTZ_L_TO_0 = {{0, 0}, {-1, 0}, {-1, -1}, {0, 2}, {-1, 2}};
    // JLSTZ — counter-clockwise
    private static final int[][] JLSTZ_R_TO_0 = {{0, 0}, {1, 0}, {1, -1}, {0, 2}, {1, 2}};
    private static final int[][] JLSTZ_2_TO_R = {{0, 0}, {-1, 0}, {-1, 1}, {0, -2}, {-1, -2}};
    private static final int[][] JLSTZ_L_TO_2 = {{0, 0}, {-1, 0}, {-1, -1}, {0, 2}, {-1, 2}};
    private static final int[][] JLSTZ_0_TO_L = {{0, 0}, {1, 0}, {1, 1}, {0, -2}, {1, -2}};

    // I — Guideline SRS (tetris.wiki)
    private static final int[][] I_0_TO_R = {{0, 0}, {-2, 0}, {1, 0}, {-2, -1}, {1, 2}};
    private static final int[][] I_R_TO_2 = {{0, 0}, {-1, 0}, {2, 0}, {-1, 2}, {2, -1}};
    private static final int[][] I_2_TO_L = {{0, 0}, {2, 0}, {-1, 0}, {2, 1}, {-1, -2}};
    private static final int[][] I_L_TO_0 = {{0, 0}, {1, 0}, {-2, 0}, {1, -2}, {-2, 1}};
    private static final int[][] I_R_TO_0 = {{0, 0}, {2, 0}, {-1, 0}, {2, 1}, {-1, -2}};
    private static final int[][] I_2_TO_R = {{0, 0}, {1, 0}, {-2, 0}, {1, -2}, {-2, 1}};
    private static final int[][] I_L_TO_2 = {{0, 0}, {-2, 0}, {1, 0}, {-2, -1}, {1, 2}};
    private static final int[][] I_0_TO_L = {{0, 0}, {-1, 0}, {2, 0}, {-1, 2}, {2, -1}};

    private static final int[][] GENERIC_FALLBACK = {
        {0, 0}, {1, 0}, {-1, 0}, {2, 0}, {-2, 0}, {0, -1}, {0, -2}, {1, -1}, {-1, -1}
    };
}
