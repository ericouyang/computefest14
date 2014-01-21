import java.util.HashSet;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Set;

class Player {

    private static int discardRow, discardCol;

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
        Wall myWall = State.getMyWall();
        int currDiscard = State.getCurrDiscard();

        double maxChange = 0;
        discardRow = -1;
        discardCol = -1;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                double oldScore = myWall.brickScore(i, j);
                double newScore = Wall.brickScore(currDiscard, i, j);
                double change = (oldScore - newScore) * 0.5 + (1 - newScore) * 0.5;
                if (change > maxChange) {
                    maxChange = change;
                    discardRow = i;
                    discardCol = j;
                }
            }
        }

        return maxChange > 0 ? "d" : "p";
    }

    /**
     * Convert numeric (row, col) to alphanumeric "XY"
     */
    public static String toCoord(int row, int col) {
        return Character.toString((char) ('A' + row)) + Character.toString((char) ('0' + col));
    }

    /**
     * Choose the coordinate to place the chosen brick into our wall.
     * Input:
     * wall:  A 2D array denoting our wall.
     * owall: A 2D array denoting the opponent's wall.
     * brick: The integer value of the chosen brick.
     * Output:
     * 'XY' where X is a char from 'A' to 'D' and Y is an char from '0' to '3'
     * Consider using toCoord to produce valid output.
     */
    public static String chooseCoord(int brick) {
        /*
        Wall myWall = State.getMyWall();

        Wall[] walls = new Wall[16];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                walls[i * 4 + j] = new Wall(myWall, brick, i, j);
            }
        }

        double minScore = Double.MAX_VALUE;
        int bestWallIdx = 0;
        for (int i = 0; i < 16; i++) {
            double score = walls[i].calcScore();
            // System.out.print(walls[i]);
            // System.out.println(score);
            if (score < minScore) {
                bestWallIdx = i;
                minScore = score;
            }
        }

        if (minScore > myWall.calcScore()) {
            return "-1";
        }

        return toCoord(bestWallIdx / 4, bestWallIdx % 4);
        */

        if (discardRow != -1) {
            return toCoord(discardRow, discardCol);
        }

        Wall myWall = State.getMyWall();

        double maxChange = 0;
        int row = -1;
        int col = -1;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                double oldScore = myWall.brickScore(i, j);
                double newScore = Wall.brickScore(brick, i, j);
                double change = (oldScore - newScore) * 0.5 + (1 - newScore) * 0.5;
                if (change > maxChange) {
                    maxChange = change;
                    row = i;
                    col = j;
                }
            }
        }

        return maxChange > 0 ? toCoord(row, col) : "-1";

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
