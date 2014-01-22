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
    private int numPairs;

    public Wall(int[][] b) {
        height = b.length;
        width = b[0].length;
        numBricks = width * height;
        numPairs = 2 * (width - 1) * height;

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
        double pairScore = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                pairScore += pairScore(i, j, i, j + 1) / numPairs;
            }
        }

        for (int j = 0; j < 4; j++) {
            for (int i = 0; i < 3; i++) {
                pairScore += pairScore(i, j, i + 1, j) / numPairs;
            }
        }

        double offsetScore = 0;
        for (int j = 0; j < 4; j++) {
            for (int i = 0; i < 4; i++) {
                offsetScore += brickOffset(i, j);
            }
        }

        return pairScore * 0.7 + offsetScore * 0.3;
    }

    public double pairScore(int i1, int j1, int i2, int j2)
    {
        double score = 0;
        if (j1 == j2 && i1 < i2)
        {
            score = Math.max(bricks[i2][j2] - bricks[i1][j1], 0);
        }
        else if (j1 == j2 && i1 > i2)
        {
            score = Math.max(bricks[i1][j1] - bricks[i2][j2], 0);
        }
        else if (i1 == i2 && j1 < j2)
        {
            score = Math.max(bricks[i2][j2] - bricks[i1][j1], 0);
        }
        else if (i1 == i2 && j1 > j2)
        {
            score = Math.max(bricks[i1][j1] - bricks[i2][j2], 0);
        }

        return score;
    }

    public double brickOffset(int i, int j)
    {
        return Math.abs(bricks[i][j] - REFERENCE_WALL[i][j]) / 100.0;
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
