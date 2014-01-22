import java.io.*;

/**
 * Created by ericouyang on 1/22/14.
 */
public class ConstantFinder {

    public static void main(String[] args) throws IOException {

        PrintWriter file = new PrintWriter("constant-finder.txt", "UTF-8");

        file.println("c1,c2,c3,result");
        file.flush();

        double incr = 0.1;

        double bestC1 = 0, bestC2 = 0, bestC3 = 0;
        double bestResult = 0;

        System.out.println("c1\tc2\tc3\tresult");

        for (double c1 = 0; c1 < 2; c1 += incr)
        {
            for (double c2 = 0; c2 < 2; c2 += incr)
            {
                for (double c3 = 0; c3 < 2; c3 += incr)
                {
                    try
                    {
                        ProcessBuilder pb1 = new ProcessBuilder(String.format("java BatchPlayer 250 java Player asdf1234 %.2f %.2f %.2f", c1, c2, c3).split(" "));
                        Process p1 = pb1.start();

                        ProcessBuilder pb2 = new ProcessBuilder("java BatchPlayer 250 java StrictPlayer asdf1234".split(" "));
                        Process p2 = pb2.start();

                        BufferedReader reader = new BufferedReader(new InputStreamReader(p1.getInputStream()));
                        String line;

                        boolean ended = false;
                        while ((line = reader.readLine()) != null) {
                            // System.out.println(line);
                            if (line.startsWith("*")) {
                                double result = Double.parseDouble(line.substring(1));

                                System.out.printf("%.2f\t%.2f\t%.2f\t%.5f\n", c1, c2, c3, result);
                                file.printf("%.2f,%.2f,%.2f,%.5f\n", c1, c2, c3, result);
                                file.flush();

                                if (result > bestResult)
                                {
                                    bestResult = result;
                                    bestC1 = c1;
                                    bestC2 = c2;
                                    bestC3 = c3;
                                }
                                ended = true;
                                break;
                            }
                        }
                        if (!ended) {
                            System.out.print("Error");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                }
            }
        }

        System.out.printf("\nResult: %.2f\t%.2f\t%.2f\t%.5f", bestC1, bestC2, bestC3, bestResult);
        file.close();
    }
}
