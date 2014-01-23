import java.util.LinkedList;
import java.util.List;

/**
 * Created by Frederick Widjaja on 1/21/14.
 */
public class RelaxedCautiousPlayerVar {

    private static final int[][] RANGES = {{86, 99}, {72, 93}, {58, 80}, {38, 61}, {19, 41}, {6, 27}, {0, 13}};
    private static final int RANGE_MIN = 0;
    private static final int RANGE_MAX = 1;

    private static final double[] REFERENCE = {99, 87, 71, 49.5, 28, 12, 0};

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
        int[][] bricks = State.getMyWall().toArray();
        int currDiscard = State.getCurrDiscard();
        for (int i = 0; i < 7; i++) {
            int[] range = RANGES[i];
            if (isWithinRange(currDiscard, range)) {
                int start = Math.max(0, i - 3);
                int end = Math.min(3, i);
                for (int j = start; j <= end; j++) {
                    int row = j;
                    int col = i - j;
                    // should swap out brick
                    if (!isWithinRange(bricks[row][col], range)) {
                        // check if brick is within overlapping region of the range
                        // then check if brick fits into place
                        if (i > 0 && currDiscard >= RANGES[i - 1][RANGE_MIN]
                                && ((row > 0 && isBeforeSmaller(bricks, row - 1, col, currDiscard))
                                || (col > 0 && isBeforeSmaller(bricks, row, col - 1, currDiscard)))) {
                            continue;
                        }
                        if (i < 6 && currDiscard <= RANGES[i + 1][RANGE_MAX]
                                && ((row < 3 && isAfterLarger(bricks, row + 1, col, currDiscard))
                                || (col < 3 && isAfterLarger(bricks, row, col + 1, currDiscard)))) {
                            continue;
                        }
                        return "d";
                    }
                }
            }
        }
        return "p";
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
        List<Coord> possibleMoves = new LinkedList<Coord>();
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
                        possibleMoves.add(new Coord(row, col));
                    }
                }
            }
        }
        if (possibleMoves.isEmpty()) {
            return INVALID_MOVE;
        } else if (possibleMoves.size() == 1) {
            return possibleMoves.get(0).toString();
        }

        List<Coord> mostRelevant = new LinkedList<Coord>();
        double bestRelevancy = Double.MAX_VALUE;
        for (Coord coord : possibleMoves) {
            double relevancy = getRelevancy(coord, brick);
            if (relevancy < bestRelevancy) {
                mostRelevant.clear();
                mostRelevant.add(coord);
                bestRelevancy = relevancy;
            }
        }
        if (mostRelevant.size() == 1) {
            return mostRelevant.get(0).toString();
        }

        Coord mostSpacedOut = null;
        double bestSpacing = Double.MAX_VALUE;
        for (Coord coord : mostRelevant) {
            double spacing = getSpacing(bricks, coord, brick);
            if (spacing < bestSpacing) {
                mostSpacedOut = coord;
                bestSpacing = spacing;
            }
        }
        return mostSpacedOut.toString();
    }

    public static double getRelevancy(Coord coord, int brick) {
        return Math.abs(brick - REFERENCE[coord.row + coord.col]);
    }

    public static double getSpacing(int[][] bricks, Coord coord, int brick) {
        double spacing = 0;

        int rowStart = -1;
        for (int row = coord.row - 1; row >= 0; row--) {
            if (bricks[row][coord.col] > brick) {
                rowStart = row;
                break;
            }
        }
        int rowEnd = 4;
        for (int row = coord.row + 1; row < 4; row++) {
            if (bricks[row][coord.col] < brick) {
                rowEnd = row;
            }
        }
        double rowStartBrick = (rowStart == -1 ? REFERENCE[++rowStart + coord.col] : bricks[rowStart][coord.col]);
        double rowEndBrick = (rowEnd == 4 ? REFERENCE[--rowEnd + coord.col] : bricks[rowEnd][coord.col]);
        double idealRowSpacing = (rowStartBrick - rowEndBrick) / (rowEnd - rowStart);
        spacing += Math.abs((coord.row - rowStart) * idealRowSpacing - (rowStartBrick - brick));
        spacing += Math.abs((rowEnd - coord.row) * idealRowSpacing - (brick - rowEndBrick));

        int colStart = -1;
        for (int col = coord.col - 1; col >= 0; col--) {
            if (bricks[coord.row][col] > brick) {
                colStart = col;
                break;
            }
        }
        int colEnd = 4;
        for (int col = coord.col + 1; col < 4; col++) {
            if (bricks[coord.row][col] < brick) {
                colEnd = col;
            }
        }
        double colStartBrick = (colStart == -1 ? REFERENCE[++colStart + coord.col] : bricks[colStart][coord.col]);
        double colEndBrick = (colEnd == 4 ? REFERENCE[--colEnd + coord.col] : bricks[colEnd][coord.col]);
        double idealColSpacing = (colStartBrick - colEndBrick) / (colEnd - colStart);
        spacing += Math.abs((coord.row - colStart) * idealColSpacing - (colStartBrick - brick));
        spacing += Math.abs((colEnd - coord.row) * idealColSpacing - (brick - colEndBrick));

        return spacing;
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java Player GAMEID");
            System.out.println("\tGAMEID = 0     creates a new game");
            System.out.println("\tGAMEID = WXYZ  connect to a specific game");
            return;
        }

        // Connect to a game with id from the command line
        Game game = new Game(args[0]);

        int deviation = Integer.parseInt(args[1]);
        for (int i = 0; i < RANGES.length; i++) {
            int[] range = RANGES[i];
            if (range[RANGE_MIN] != 0) {
                range[RANGE_MIN] = range[RANGE_MIN] + deviation;
            }
            if (range[RANGE_MAX] != 99) {
                range[RANGE_MAX] = range[RANGE_MAX] - deviation;
            }
        }

        State.init();

        while (true) {
            // On our turn, we get the brick on the pile
            int discardBrick = game.getDiscard();
            State.update(game.wall, game.owall, discardBrick);
            State.print();

            // Choose 'p' to get the pile brick or 'd' to get the discard brick
            String pd_move = chooseMove();
            System.out.println(pd_move);

            // Get the brick we chose (either the pile brick or the discard brick)
            int brick = game.getBrick(pd_move);

            // Determine where to place this brick with row-col coords "A0", "B3", etc
            String co_move = chooseCoord(brick);
            System.out.println(co_move);

            // Make the move -- informs the opponent and updates the wall
            game.makeMove(co_move);
        }
    }

    private static class Coord {
        final int row;
        final int col;

        Coord(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public String toString() {
            return Character.toString((char) ('A' + row)) + Integer.toString(col);
        }
    }
}
