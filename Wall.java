/**
 * Created by ericouyang on 1/21/14.
 */
public class Wall {
    public static final int[][] REFERENCE_WALL =
        {
            {97, 87, 71, 50},
            {87, 71, 50, 28},
            {71, 50, 28, 12},
            {50, 28, 12,  2}
        };

    private int[][] bricks;
    private int width;
    private int height;
    private int numBricks;

    public Wall(int[][] b) {
        height = b.length;
        width = b[0].length;
        numBricks = width * height;

        bricks = new int[height][width];
        for (int i = 0; i < height; i++) {
            System.arraycopy(b[i], 0, bricks[i], 0, width);
        }
    }

    public Wall(Wall w, int discard, int i, int j){
        this(w.bricks);
        this.bricks[i][j] = discard;
    }

    public void update(int[][] b) {
        bricks = b;
    }

    public int[][] toArray() {
        return bricks;
    }

    public double calcScore() {
        double score = 0;

        for (int i = bricks.length - 1; i > 0; --i) {
            for (int j = 0, cols = bricks[i].length; j < cols; ++j) {
                score += brickScore(i, j) / numBricks;
            }
        }

        return score;
    }

    public double brickScore(int i, int j) {
        return calcBrickScoreWithValue(bricks[i][j], i, j);
    }

    public double calcBrickScoreWithValue(int brick, int i, int j) {
        double offset = Math.abs(brick - REFERENCE_WALL[i][j]) / 100.0;

        double incorrectness = 0;
        int numAdjacent = 0;

        if (i + 1 <= 3)
        {
            numAdjacent++;
            if (bricks[i + 1][j] > brick)
                incorrectness += bricks[i + 1][j] - brick;
        }
        if (j + 1 <= 3)
        {
            numAdjacent++;
            if (bricks[i][j + 1] > brick)
                incorrectness += bricks[i][j + 1] - brick;
        }
        if (i - 1 >= 0)
        {
            numAdjacent++;
            if (bricks[i - 1][j] < brick)
                incorrectness += brick - bricks[i - 1][j];
        }
        if (j - 1 >= 0)
        {
            numAdjacent++;
            if (bricks[i][j - 1] < brick)
                incorrectness +=  brick - bricks[i][j - 1];
        }
        incorrectness /= numAdjacent * 100;

        return offset * 0.6 + incorrectness * 0.4;
    }

    /**
     * Pretty print a game wall
     */
    public String toString() {
        StringBuilder s = new StringBuilder(260);

        s.append("  ||");
        for (int i = 0; i < bricks[0].length; ++i) s.append(String.format(" %2d |", i));
        s.append("|\n" + new String(new char[4 + 5 * bricks[0].length + 1]).replace("\0", "=") + "\n");
        for (int i = bricks.length - 1; i > 0; --i) {
            s.append(String.format("%c ||", 'A' + i));
            for (int j = 0; j < bricks[i].length; ++j) s.append(String.format(" %2d |", bricks[i][j]));
            s.append("|\t");

            for (int j = 0; j < bricks[i].length; ++j) s.append(String.format(" %.2f |", brickScore(i, j)));
            s.append("|\n");

            s.append("--||");
            for (int j = 0; j < bricks[i].length; ++j) s.append("----|");
            s.append("|\t");

            for (int j = 0; j < bricks[i].length; ++j) s.append("------|");
            s.append("|\n");
        }
        s.append(String.format("%c ||", 'A'));
        for (int j = 0; j < bricks[0].length; ++j) s.append(String.format(" %2d |", bricks[0][j]));
        s.append("\t");
        for (int j = 0; j < bricks[0].length; ++j) s.append(String.format(" %.2f |", brickScore(0, j)));
        s.append("|\n" + new String(new char[4 + 5 * bricks[0].length + 1]).replace("\0", "=") + "\n");

        return s.toString();
    }
}
