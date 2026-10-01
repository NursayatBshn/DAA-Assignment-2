package daa.bench;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Locale;

public class CsvWriter {
    public static void writeResults(String filename, List<Result> results) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
            pw.println("workload,variant,structure,n,time_ms,steps,moves,comparisons");
            for (Result r : results) {
                // Use Locale.US to force dot (.) as decimal separator instead of comma (,)
                pw.printf(Locale.US, "%s,%s,%s,%d,%.3f,%d,%d,%d%n",
                        r.workload, r.variant, r.structure, r.n, r.timeMs, r.steps, r.moves, r.comparisons);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
