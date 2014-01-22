import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;

public class BatchPlayer {

    public static void main(String[] args) {
        int numTrials = Integer.parseInt(args[0]);
        String[] cmd = Arrays.copyOfRange(args, 1, args.length);

        System.out.println("Running process " + cmd[1]);
        int numWins = 0;
        int numLosses = 0;
        for (int i = 1; i <= numTrials; i++) {
            try {
                System.out.print("Trial " + i + "; Result: ");
                boolean ended = false;

                ProcessBuilder pb = new ProcessBuilder(cmd);
                Process p = pb.start();

                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    // System.out.println(line);
                    if (line.equals("***WIN***")) {
                        numWins++;
                        System.out.println("Win");
                        ended = true;
                        break;
                    } else if (line.equals("***LOSE***")) {
                        numLosses++;
                        System.out.println("Loss");
                        ended = true;
                        break;
                    }
                }
                if (!ended) {
                    System.out.println("Error");
                }

                p.destroy();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }

        System.out.println("Wins: " + numWins);
        System.out.println("Losses: " + numLosses);
        System.out.println("% Wins: " + (numWins * 100.0 / (numWins + numLosses)) + "%");
    }

}
