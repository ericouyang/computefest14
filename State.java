import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class State {

    private static Wall myWall;
    private static Wall oppWall;
    private static int currDiscard;

    private static Set<Integer> unknownBricks = new HashSet<Integer>();
    private static LinkedList<Integer> stack = new LinkedList<Integer>();

    public static void init() {
        for (int i = 0; i < 100; i++) {
            unknownBricks.add(i);
        }
    }

    public static void removeUnknownBricks(int[][] wall) {
        for (int row = 0; row < wall.length; row++) {
            for (int col = 0; col < wall[row].length; col++) {
                unknownBricks.remove(wall[row][col]);
            }
        }
    }

    public static void update(int[][] wall,
                                   int[][] owall,
                                   int discardBrick) {
        if (myWall == null) {
            myWall = new Wall(wall);
        } else {
            myWall.update(wall);
        }
        if (oppWall == null) {
            oppWall = new Wall(owall);
        } else {
            oppWall.update(owall);
        }
        currDiscard = discardBrick;

        removeUnknownBricks(wall);
        removeUnknownBricks(owall);
        unknownBricks.remove(discardBrick);

        if (stack.size() > 0) {
            int first = stack.getFirst();

            if (first == discardBrick) {
                return;
            }

            outer:
            for (int row = 0; row < owall.length; row++) {
                for (int col = 0; col < owall[row].length; col++) {
                    if (first == owall[row][col]) {
                        stack.removeFirst();
                        break outer;
                    }
                }
            }
        }

        stack.addFirst(discardBrick);
    }

    public static Wall getMyWall() {
        return myWall;
    }

    public static Wall getOppWall() {
        return oppWall;
    }

    public static int getCurrDiscard() {
        return currDiscard;
    }

    public static Set<Integer> getUnknownBricks() {
        return unknownBricks;
    }

    public static LinkedList<Integer> getStack() {
        return stack;
    }

}
