package classic;

/** Receives score, level, line, and high score changes. */
public interface ScoreListener {
    /** Called whenever the visible game stats change. */
    void onScoreChanged(int score, int level, int lines, int highScore);
}