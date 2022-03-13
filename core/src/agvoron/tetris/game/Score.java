package agvoron.tetris.game;

public class Score {

    private static final int CLEAR = 1000;
    private static final int SOFT_DROP = 5;
    private static final int HARD_DROP = 5;
    private static final float COMBO_SCALAR = 0.5f;

    private static int score;
    private static int combo;

    public static int getScore() {
        return score;
    }

    public static void reset() {
        score = 0;
        combo = 0;
    }

    public static void addScore(int add) {
        score += add;
    }

    public static void clearLines(int cleared) {
        score += CLEAR * Math.pow(2, cleared - 1) * (1f + ((combo - 1) * COMBO_SCALAR));
    }

    public static void addCombo() {
        combo += 1;
    }

    public static void clearCombo() {
        combo = 0;
    }

    public static void trickleSoftDrop() {
        score += SOFT_DROP;
    }

    public static void hardDrop(int distance) {
        score += HARD_DROP * distance;
    }

}
