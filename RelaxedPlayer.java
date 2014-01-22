/**
 * Created by Frederick Widjaja on 1/21/14.
 */
public class RelaxedPlayer {

    private static final int[][] RANGES = {
            {86, 99}, {72, 93}, {58, 80}, {38, 61}, {19, 41}, {6, 27}, {0, 13}
    };
    private static final int RANGE_MIN = 0;
    private static final int RANGE_MAX = 1;

    public static final String INVALID_MOVE = "-1";


    public static boolean isWithinRange(int brick, int[] range) {
        return brick >= range[RANGE_MIN] && brick <= range[RANGE_MAX];
    }

    public static boolean isAfterLarger(int[][] bricks, int row, int col, int brick) {
        int after = bricks[row][col];
        int[] range = RANGES[row + col];
        return isWithinRange(after, range) && after > brick;
    }

    public static boolean isBeforeSmaller(int[][] bricks, int row, int col, int brick) {
        int before = bricks[row][col];
        int[] range = RANGES[row + col];
        return isWithinRange(before, range) && before < brick;
    }

    /**
     * Choose the known brick in the discard or a unknown brick in the pile.
     * Input:
     * wall:  A 2D array denoting our game wall.
     * owall: A 2D array denoting the opponent's game wall.
     * discardBrick: The integer value of the known brick in the discard.
     * Output:
     * 'd': Accept the known brick in the discard.
     * 'p': Reject the known brick in the discard and draw from the pile.
     */
    public static String chooseMove() {
        // check if the current discard brick yields a feasible move
        return chooseCoord(State.getCurrDiscard()).equals(INVALID_MOVE) ? "p" : "d";
    }

    /**
     * Choose the coordinate to place the brick brick into our wall.
     * Input:
     * wall:  A 2D array denoting our wall.
     * owall: A 2D array denoting the opponent's wall.
     * brick: The integer value of the brick brick.
     * Output:
     * 'XY' where X is a char from 'A' to 'D' and Y is an char from '0' to '3'
     * Consider using toCoord to produce valid output.
     */
    public static String chooseCoord(int brick) {
        int[][] bricks = State.getMyWall().toArray();
        for (int i = 0; i < 7; i++) {
            int[] range = RANGES[i];
            if (isWithinRange(brick, range)) {
                int start = Math.max(0, i - 3);
                int end = Math.min(3, i);
                for (int j = start; j <= end; j++) {
                    int row = j;
                    int col = i - j;
                    // should swap out brick
                    if (!isWithinRange(bricks[row][col], range)) {
                        // check if brick is within overlapping region of the range
                        // then check if brick fits into place
                        if (i > 0 && brick >= RANGES[i - 1][RANGE_MIN]
                                && ((row > 0 && isBeforeSmaller(bricks, row - 1, col, brick))
                                || (col > 0 && isBeforeSmaller(bricks, row, col - 1, brick)))) {
                            continue;
                        }
                        if (i < 6 && brick <= RANGES[i + 1][RANGE_MAX]
                                && ((row < 3 && isAfterLarger(bricks, row + 1, col, brick))
                                || (col < 3 && isAfterLarger(bricks, row, col + 1, brick)))) {
                            continue;
                        }
                        return toCoord(row, col);
                    }
                }
            }
        }
        return INVALID_MOVE;
    }

    /**
     * Convert numeric (row, col) to alphanumeric "XY"
     */
    public static String toCoord(int row, int col) {
        return Character.toString((char) ('A' + row)) + Integer.toString(col);
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Player GAMEID");
            System.out.println("\tGAMEID = 0     creates a new game");
            System.out.println("\tGAMEID = WXYZ  connect to a specific game");
            return;
        }

        // Connect to a FoosGame with id from the command line
        Game game = new Game(args[0]);

        State.init();

        while (true) {
            // On our turn, we get the brick on the pile
            int discardBrick = game.getDiscard();
            State.update(game.wall, game.owall, discardBrick);
            State.print();

            // Choose 'p' to get the pile brick or 'd' to get a random brick
            String pd_move = chooseMove();
            System.out.println(pd_move);

            // Get the brick we chose (either the pile brick or a random brick)
            int brick = game.getBrick(pd_move);

            // Determine where to place this brick with row-col coords "A0", "B3", etc
            String co_move = chooseCoord(brick);
            System.out.println(co_move);

            // Make the move -- informs the opponent and updates the wall
            game.makeMove(co_move);
        }
    }
}
