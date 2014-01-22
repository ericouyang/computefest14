import java.util.HashSet;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Set;

class StrictPlayer {

    private static int[][][] allowed = {
            {{94, 99}, {81, 93}, {62, 80}, {38, 61}},
            {{81, 93}, {62, 80}, {38, 61}, {19, 37}},
            {{62, 80}, {38, 61}, {19, 37}, { 6, 18}},
            {{38, 61}, {19, 37}, { 6, 18}, { 0,  5}}
    };

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
        int[][] bricks = State.getMyWall().toArray();
        int currDiscard = State.getCurrDiscard();
        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < 4; j++) {
                int brick = bricks[i][j];
                if (currDiscard >= allowed[i][j][0] && currDiscard <= allowed[i][j][1] &&
                        (brick < allowed[i][j][0] || brick > allowed[i][j][1])) {
                    return "d";
                }
            }
        }
        return "p";
    }

    /**
     * Convert numeric (row, col) to alphanumeric "XY"
     */
    public static String toCoord(int row, int col) {
        return Character.toString((char) ('A' + row)) + Integer.toString(col);
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
    public static String chooseCoord(int chosen) {
        int[][] bricks = State.getMyWall().toArray();
        // find a correctable error
        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < 4; j++) {
                int brick = bricks[i][j];
                if (chosen >= allowed[i][j][0] && chosen <= allowed[i][j][1] &&
                        (brick < allowed[i][j][0] || brick > allowed[i][j][1])) {
                    return toCoord(i, j);
                }
            }
        }
        return "-1";
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
