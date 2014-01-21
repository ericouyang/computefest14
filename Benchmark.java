import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;

/**
 * Created by ericouyang on 1/21/14.
 */
public class Benchmark {

    final static String SESSION_NAME = "letswin";

    public static void main(String[] args)
    {
        int numTrials = Integer.parseInt(args[0]);
        int numWins = 0;

        try
        {
            for (int i = 0; i < numTrials; i++)
            {
                ProcessBuilder pB1 = new ProcessBuilder("java", "Player", SESSION_NAME);
                //pB1.redirectOutput(ProcessBuilder.Redirect.INHERIT);
                Process p1 = pB1.start();

                ProcessBuilder pB2 = new ProcessBuilder("baseline/strict_player", SESSION_NAME);
                //pB2.redirectOutput(ProcessBuilder.Redirect.INHERIT);
                Process p2 = pB2.start();

                BufferedReader reader = new BufferedReader(new InputStreamReader(p1.getInputStream()));
                String line;
                boolean won = false;
                while ((line = reader.readLine()) != null) {
                    //System.out.println(line);
                    if (line.contains("WIN")) {
                        numWins++;
                        won = true;
                    }
                }

                System.out.println("Trial " + i + "; Result: " + ((won) ? "Win" : "Loss"));
            }

            System.out.println("Wins: " + numWins);
            System.out.println("Losses: " + (numTrials - numWins));
            System.out.println("% Wins: " + ((numWins * 100.0) / numTrials) + "%");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }
}
