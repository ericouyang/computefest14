/**
 * Created by ericouyang on 1/21/14.
 */
public class Wall {
    public static final int[][] REFERENCE_WALL =
        {
            {99, 87, 71, 50},
            {87, 71, 50, 28},
            {71, 50, 28, 12},
            {50, 28, 12,  0}
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
        return brickScore(bricks[i][j], i, j);
    }

    public static double brickScore(int brick, int i, int j) {
        return Math.abs(brick - REFERENCE_WALL[i][j]) / 100.0;
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
