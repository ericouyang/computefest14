import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;

public class BatchPlayer {

    public static void main(String[] args) {
        int numTrials = Integer.parseInt(args[0]);
        String[] cmd = Arrays.copyOfRange(args, 1, args.length);

        System.out.println("Running process \"" + join(cmd) + "\" for " + (numTrials == 0 ? "infinite" : numTrials) + " iterations");
        System.out.println("No.\tResult\tWins\t\tLosses\t\t% Wins");
        int numWins = 0;
        int numLosses = 0;
        for (int i = 1; numTrials == 0 || i <= numTrials; i++) {
            try {
                System.out.print(i + "\t");
                boolean ended = false;

                ProcessBuilder pb = new ProcessBuilder(cmd);
                Process p = pb.start();

                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    // System.out.println(line);
                    if (line.equals("***WIN***")) {
                        numWins++;
                        System.out.print("Win");
                        ended = true;
                        break;
                    } else if (line.equals("***LOSE***")) {
                        numLosses++;
                        System.out.print("Loss");
                        ended = true;
                        break;
                    }
                }
                if (!ended) {
                    System.out.print("Error");
                }

                System.out.print("\t" + numWins);
                System.out.print("\t\t" + numLosses);
                System.out.println("\t\t" + (numWins * 100.0 / (numWins + numLosses)) + "%");

                p.destroy();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
        System.out.println("Total Successful Trials: " + (numWins + numLosses));
        System.out.println("Wins: " + numWins);
        System.out.println("Losses: " + numLosses);
        System.out.println("% Wins: " + (numWins * 100.0 / (numWins + numLosses)) + "%");
    }

    private static String join(String[] s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length; i++) {
            if (i > 0) {
                sb.append(" ");
            }
            sb.append(s[i]);
        }
        return sb.toString();
    }

}