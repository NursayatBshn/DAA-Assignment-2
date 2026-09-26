package daa.metrics;

public class Metrics {
    private long steps = 0;
    private long moves = 0;
    private long comparisons = 0;

    public void addStep() {
        steps++;
    }

    public void addMove() {
        moves++;
    }

    public void addComparison() {
        comparisons++;
    }

    public long getSteps() {
        return steps;
    }

    public long getMoves() {
        return moves;
    }

    public long getComparisons() {
        return comparisons;
    }

    public void reset() {
        steps = 0;
        moves = 0;
        comparisons = 0;
    }
}
