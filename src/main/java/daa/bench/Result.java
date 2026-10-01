package daa.bench;

public class Result {
    public String workload;
    public String variant;
    public String structure;
    public int n;
    public double timeMs;
    public long steps;
    public long moves;
    public long comparisons;

    public Result(String workload, String variant, String structure, int n, double timeMs, long steps, long moves, long comparisons) {
        this.workload = workload;
        this.variant = variant;
        this.structure = structure;
        this.n = n;
        this.timeMs = timeMs;
        this.steps = steps;
        this.moves = moves;
        this.comparisons = comparisons;
    }
}
