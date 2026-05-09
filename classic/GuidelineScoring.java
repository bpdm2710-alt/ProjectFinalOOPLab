package classic;

public class GuidelineScoring implements ScoringStrategy {
    private static final int[] BASE = {0, 100, 300, 500, 800};

    @Override
    public int calculate(int clearedLines, int level) {
        if (clearedLines < 0 || clearedLines >= BASE.length) {
            return 0;
        }
        return BASE[clearedLines] * Math.max(level, 1);
    }
}
