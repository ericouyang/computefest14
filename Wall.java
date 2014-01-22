/**
 * Created by ericouyang on 1/21/14.
 */
public class Wall {
    private static final int[][] REFERENCE_WALL =
        {
            {99, 87, 71, 50},
            {87, 71, 50, 28},
            {71, 50, 28, 12},
            {50, 28, 12,  0}
        };

    private static final double[] REFERENCE = {99, 87, 71, 49.5, 28, 12, 0};

    private int[][] bricks;
    private int width;
    private int height;
    private int numBricks;
    private int numPairs;

    public double pairScore;
    public double offsetScore;
    public double spacingScore;

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
        pairScore = 0;
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

        offsetScore = 0;
        for (int j = 0; j < 4; j++) {
            for (int i = 0; i < 4; i++) {
                offsetScore += brickOffset(i, j);
            }
        }

        spacingScore = 0;
        for (int j = 0; j < 4; j++) {
            for (int i = 0; i < 4; i++) {
                spacingScore += spacingScore(i, j);
            }
        }

        return pairScore + offsetScore * 0.4 + spacingScore * 0.1;
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

    public double spacingScore(int i, int j) {
        int brick = bricks[i][j];

        double spacing = 0;

        int rowStart = -1;
        for (int row = i - 1; row >= 0; row--) {
            if (bricks[row][j] > brick) {
                rowStart = row;
                break;
            }
        }
        int rowEnd = 4;
        for (int row = i + 1; row < 4; row++) {
            if (bricks[row][j] < brick) {
                rowEnd = row;
            }
        }
        double rowStartBrick = (rowStart == -1 ? REFERENCE[++rowStart + j] : bricks[rowStart][j]);
        double rowEndBrick = (rowEnd == 4 ? REFERENCE[--rowEnd + j] : bricks[rowEnd][j]);
        double idealRowSpacing = (rowStartBrick - rowEndBrick) / (rowEnd - rowStart);
        spacing += Math.abs((i - rowStart) * idealRowSpacing - (rowStartBrick - brick));
        spacing += Math.abs((rowEnd - i) * idealRowSpacing - (brick - rowEndBrick));

        int colStart = -1;
        for (int col = j - 1; col >= 0; col--) {
            if (bricks[i][col] > brick) {
                colStart = col;
                break;
            }
        }
        int colEnd = 4;
        for (int col = j + 1; col < 4; col++) {
            if (bricks[i][col] < brick) {
                colEnd = col;
            }
        }
        double colStartBrick = (colStart == -1 ? REFERENCE[++colStart + j] : bricks[colStart][j]);
        double colEndBrick = (colEnd == 4 ? REFERENCE[--colEnd + j] : bricks[colEnd][j]);
        double idealColSpacing = (colStartBrick - colEndBrick) / (colEnd - colStart);
        spacing += Math.abs((i - colStart) * idealColSpacing - (colStartBrick - brick));
        spacing += Math.abs((colEnd - i) * idealColSpacing - (brick - colEndBrick));

        return spacing / 200;
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
        return "";
        /*
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
        */
    }
}
