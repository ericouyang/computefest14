/**
 * Created by ericouyang on 1/21/14.
 */
public class Wall {
    public int[][] referenceWall =
        {
            {50, 40, 30, 20},
            {60, 50, 40, 30},
            {70, 60, 50, 20},
            {80, 70, 60, 50}
        };

    private int[][] bricks;
    private int width;
    private int height;
    private int numBricks;

    public Wall(int[][] b) {
        bricks = b;

        height = b.length;
        width = b[0].length;
        numBricks = width * height;
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
        return Math.abs(bricks[i][j] - referenceWall[i][j]) / 100;
    }
}
