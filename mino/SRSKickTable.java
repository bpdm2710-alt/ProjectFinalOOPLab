package mino;

/**
 * Super Rotation System (SRS) wall kick offsets from Tetris Guideline.
 * Offsets are in block/cell units (same as {@link Block#SIZE} steps): x right, y down.
 */
public final class SRSKickTable {
    private SRSKickTable() {}

    // JLSTZ — clockwise
    private static final int[][] JLSTZ_CW_01 = {{0, 0}, {-1, 0}, {-1, 1}, {0, -2}, {-1, -2}};
    private static final int[][] JLSTZ_CW_12 = {{0, 0}, {1, 0}, {1, -1}, {0, 2}, {1, 2}};
    private static final int[][] JLSTZ_CW_23 = {{0, 0}, {1, 0}, {1, 1}, {0, -2}, {1, -2}};
    private static final int[][] JLSTZ_CW_30 = {{0, 0}, {-1, 0}, {-1, -1}, {0, 2}, {-1, 2}};

    // JLSTZ — counter-clockwise
    private static final int[][] JLSTZ_CCW_10 = {{0, 0}, {1, 0}, {1, -1}, {0, 2}, {1, 2}};
    private static final int[][] JLSTZ_CCW_21 = {{0, 0}, {-1, 0}, {-1, 1}, {0, -2}, {-1, -2}};
    private static final int[][] JLSTZ_CCW_32 = {{0, 0}, {-1, 0}, {-1, -1}, {0, 2}, {-1, 2}};
    private static final int[][] JLSTZ_CCW_03 = {{0, 0}, {1, 0}, {1, 1}, {0, -2}, {1, -2}};

    // I — clockwise
    private static final int[][] I_CW_01 = {{0, 0}, {-2, 0}, {1, 0}, {-2, -1}, {1, 2}};
    private static final int[][] I_CW_12 = {{0, 0}, {-1, 0}, {2, 0}, {-1, 2}, {2, -2}};
    private static final int[][] I_CW_23 = {{0, 0}, {2, 0}, {-1, 0}, {2, 1}, {-1, -2}};
    private static final int[][] I_CW_30 = {{0, 0}, {-2, 0}, {1, 0}, {-2, 1}, {1, -2}};

    // I — counter-clockwise
    private static final int[][] I_CCW_10 = {{0, 0}, {2, 0}, {-1, 0}, {2, -1}, {-1, 2}};
    private static final int[][] I_CCW_21 = {{0, 0}, {1, 0}, {-2, 0}, {1, -2}, {-2, 1}};
    private static final int[][] I_CCW_32 = {{0, 0}, {-2, 0}, {1, 0}, {-2, 1}, {1, -2}};
    private static final int[][] I_CCW_03 = {{0, 0}, {-1, 0}, {2, 0}, {-1, 2}, {2, -1}};

    private static final int[][] GENERIC = {{0, 0}, {1, 0}, {-1, 0}, {2, 0}, {-2, 0}, {0, -1}, {0, -2}, {1, -1}, {-1, -1}};

    public static int[][] getJLSTZKicks(int oldState, int newState) {
        boolean cw = newState == (oldState + 1) % 4;
        if (cw) {
            switch (oldState) {
                case 0: return JLSTZ_CW_01;
                case 1: return JLSTZ_CW_12;
                case 2: return JLSTZ_CW_23;
                case 3: return JLSTZ_CW_30;
                default: return GENERIC;
            }
        }
        if (newState == (oldState + 3) % 4) {
            switch (oldState) {
                case 0: return JLSTZ_CCW_03;
                case 1: return JLSTZ_CCW_10;
                case 2: return JLSTZ_CCW_21;
                case 3: return JLSTZ_CCW_32;
                default: return GENERIC;
            }
        }
        return GENERIC;
    }

    public static int[][] getIKicks(int oldState, int newState) {
        boolean cw = newState == (oldState + 1) % 4;
        if (cw) {
            switch (oldState) {
                case 0: return I_CW_01;
                case 1: return I_CW_12;
                case 2: return I_CW_23;
                case 3: return I_CW_30;
                default: return GENERIC;
            }
        }
        if (newState == (oldState + 3) % 4) {
            switch (oldState) {
                case 0: return I_CCW_03;
                case 1: return I_CCW_10;
                case 2: return I_CCW_21;
                case 3: return I_CCW_32;
                default: return GENERIC;
            }
        }
        return GENERIC;
    }
}
