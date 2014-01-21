import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

class Player {

    Set<Integer> unknownBricks = new HashSet<Integer>();
    LinkedList<Integer> brickStack = new LinkedList<Integer>();

    public static void init() {

    }

    public static void updateStack(int[][] wall,
                                   int[][] owall,
                                   int discardBrick) {

    }

    /**
     * Pretty print a game wall
     */
    public static void printWall(int[][] s) {
        System.out.format("  ||");
        for (int i = 0; i < s[0].length; ++i) System.out.format(" %2d |", i);
        System.out.format("|\n" + new String(new char[4 + 5 * s[0].length + 1]).replace("\0", "=") + "\n");
        for (int i = s.length - 1; i > 0; --i) {
            System.out.format("%c ||", 'A' + i);
            for (int j = 0; j < s[i].length; ++j) System.out.format(" %2d |", s[i][j]);
            System.out.format("|\n");

            System.out.format("--||");
            for (int j = 0; j < s[i].length; ++j) System.out.format("----|");
            System.out.format("|\n");
        }
        System.out.format("%c ||", 'A');
        for (int j = 0; j < s[0].length; ++j) System.out.format(" %2d |", s[0][j]);
        System.out.format("|\n" + new String(new char[4 + 5 * s[0].length + 1]).replace("\0", "=") + "\n");
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
    public static String chooseMove(int[][] wall,
                                    int[][] owall,
                                    int discardBrick) {
        System.out.println("\nOpponent:");
        printWall(owall);
        System.out.println("\nMy Wall:");
        printWall(wall);

        System.out.format("\np: **  d: %d\n", discardBrick);
        System.out.print("Pile or Discard: ");
        String s = System.console().readLine();
        return s;
    }

    /**
     * Convert numeric (row, col) to alphanumeric "XY"
     */
    public static String toCoord(int row, int col) {
        return "" + ('A' + row) + ('0' + col);
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
    public static String chooseCoord(int[][] wall,
                                     int[][] owall,
                                     int brick) {
        System.out.println(brick);
        System.out.print("Coord: ");
        String move = System.console().readLine();
        return move;
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

        init();

        while (true) {
            // On our turn, we get the brick on the pile
            int discardBrick = game.getDiscard();

            updateStack(game.wall, game.owall, discardBrick);

            // Choose 'p' to get the pile brick or 'd' to get a random brick
            String pd_move = chooseMove(game.wall, game.owall, discardBrick);

            // Get the brick we chose (either the pile brick or a random brick)
            int brick = game.getBrick(pd_move);

            // Determine where to place this brick with row-col coords "A0", "B3", etc
            String co_move = chooseCoord(game.wall, game.owall, brick);

            // Make the move -- informs the opponent and updates the wall
            game.makeMove(co_move);
        }
    }
}
