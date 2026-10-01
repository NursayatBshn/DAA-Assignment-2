package daa.bench;

import daa.metrics.Metrics;
import daa.structures.DynamicArray;
import daa.structures.MinHeap;
import daa.structures.MyCollection;
import daa.structures.MyLinkedList;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.function.Supplier;

public class Benchmark {
    private static final int[] SIZES = {100, 1000, 10000, 100000};
    private static final int WARMUP_RUNS = 5;
    private static final int MEASURE_RUNS = 5;

    public static List<Result> runAll() {
        List<Result> results = new ArrayList<>();
        for (int n : SIZES) {
            System.out.println("Running benchmarks for n = " + n);
            runW1(n, results);
            runW2(n, results);
            runW3(n, "head", results);
            runW3(n, "middle", results);
            runW4(n, results);
        }

        // Run Bonus Task B Benchmark: Floyd's buildHeap vs n Inserts
        runBonusTaskB();

        return results;
    }

    private static void runW1(int n, List<Result> results) {
        Random rand = new Random(42);
        int[] data = new int[n];
        for (int i = 0; i < n; i++) data[i] = rand.nextInt();

        int[] queries = new int[10000];
        for (int i = 0; i < 10000; i++) queries[i] = rand.nextInt(n);

        // DynamicArray
        benchmarkTimedOperation("W1", "-", "DynamicArray", n, () -> {
            Metrics m = new Metrics();
            DynamicArray arr = new DynamicArray(m);
            for (int x : data) arr.add(x);
            return new BenchmarkTask(m, () -> {
                for (int q : queries) {
                    arr.get(q);
                }
            });
        }, results);

        // MyLinkedList
        benchmarkTimedOperation("W1", "-", "MyLinkedList", n, () -> {
            Metrics m = new Metrics();
            MyLinkedList list = new MyLinkedList(m);
            for (int x : data) list.add(x);
            return new BenchmarkTask(m, () -> {
                for (int q : queries) {
                    list.get(q);
                }
            });
        }, results);
    }

    private static void runW2(int n, List<Result> results) {
        Random rand = new Random(42);
        int[] data = new int[n];
        for (int i = 0; i < n; i++) data[i] = rand.nextInt();

        int[] queries = new int[1000];
        for (int i = 0; i < 500; i++) queries[i] = data[rand.nextInt(n)]; // present
        for (int i = 500; i < 1000; i++) queries[i] = rand.nextInt(); // random / not present

        // Shuffle queries
        for (int i = 0; i < queries.length; i++) {
            int swapIdx = rand.nextInt(queries.length);
            int temp = queries[i];
            queries[i] = queries[swapIdx];
            queries[swapIdx] = temp;
        }

        // DynamicArray
        benchmarkTimedOperation("W2", "-", "DynamicArray", n, () -> {
            Metrics m = new Metrics();
            DynamicArray arr = new DynamicArray(m);
            for (int x : data) arr.add(x);
            return new BenchmarkTask(m, () -> {
                for (int q : queries) {
                    arr.contains(q);
                }
            });
        }, results);

        // MyLinkedList
        benchmarkTimedOperation("W2", "-", "MyLinkedList", n, () -> {
            Metrics m = new Metrics();
            MyLinkedList list = new MyLinkedList(m);
            for (int x : data) list.add(x);
            return new BenchmarkTask(m, () -> {
                for (int q : queries) {
                    list.contains(q);
                }
            });
        }, results);
    }

    private static void runW3(int n, String variant, List<Result> results) {
        Random rand = new Random(42);
        int[] data = new int[n];
        for (int i = 0; i < n; i++) data[i] = rand.nextInt();

        int[] inserts = new int[1000];
        for (int i = 0; i < 1000; i++) inserts[i] = rand.nextInt();

        int index = variant.equals("head") ? 0 : n / 2;

        // DynamicArray
        benchmarkTimedOperation("W3", variant, "DynamicArray", n, () -> {
            Metrics m = new Metrics();
            DynamicArray arr = new DynamicArray(m);
            for (int x : data) arr.add(x);
            return new BenchmarkTask(m, () -> {
                for (int i = 0; i < 1000; i++) arr.add(index, inserts[i]);
                for (int i = 0; i < 1000; i++) arr.remove(index);
            });
        }, results);

        // MyLinkedList
        benchmarkTimedOperation("W3", variant, "MyLinkedList", n, () -> {
            Metrics m = new Metrics();
            MyLinkedList list = new MyLinkedList(m);
            for (int x : data) list.add(x);
            return new BenchmarkTask(m, () -> {
                for (int i = 0; i < 1000; i++) list.add(index, inserts[i]);
                for (int i = 0; i < 1000; i++) list.remove(index);
            });
        }, results);
    }

    private static void runW4(int n, List<Result> results) {
        Random rand = new Random(42);
        int[] data = new int[n];
        for (int i = 0; i < n; i++) data[i] = rand.nextInt();

        benchmarkTimedOperation("W4", "-", "MinHeap", n, () -> {
            Metrics m = new Metrics();
            MinHeap heap = new MinHeap(m);
            return new BenchmarkTask(m, () -> {
                for (int x : data) {
                    heap.insert(x);
                }
                int prev = heap.extractMin();
                for (int i = 1; i < n; i++) {
                    int cur = heap.extractMin();
                    if (prev > cur) {
                        throw new IllegalStateException("Heap output not in non-decreasing order: " + prev + " > " + cur);
                    }
                    prev = cur;
                }
            });
        }, results);
    }

    private static void runBonusTaskB() {
        System.out.println("Running Bonus Task B (buildHeap vs n inserts)...");
        try (PrintWriter pw = new PrintWriter(new FileWriter("results/buildheap_comparison.csv"))) {
            pw.println("method,n,time_ms,steps,moves,comparisons");

            for (int n : SIZES) {
                Random rand = new Random(42);
                int[] data = new int[n];
                for (int i = 0; i < n; i++) data[i] = rand.nextInt();

                // Floyd's buildHeap
                double[] buildHeapTimes = new double[MEASURE_RUNS];
                Metrics bhMetrics = null;
                for (int r = 0; r < WARMUP_RUNS + MEASURE_RUNS; r++) {
                    Metrics m = new Metrics();
                    long start = System.nanoTime();
                    MinHeap.buildHeap(data, m);
                    long end = System.nanoTime();
                    if (r >= WARMUP_RUNS) {
                        buildHeapTimes[r - WARMUP_RUNS] = (end - start) / 1_000_000.0;
                        bhMetrics = m;
                    }
                }
                Arrays.sort(buildHeapTimes);
                double bhMedian = buildHeapTimes[buildHeapTimes.length / 2];
                pw.printf(Locale.US, "buildHeap,%d,%.3f,%d,%d,%d%n",
                        n, bhMedian, bhMetrics.getSteps(), bhMetrics.getMoves(), bhMetrics.getComparisons());

                // n inserts
                double[] insertTimes = new double[MEASURE_RUNS];
                Metrics insMetrics = null;
                for (int r = 0; r < WARMUP_RUNS + MEASURE_RUNS; r++) {
                    Metrics m = new Metrics();
                    MinHeap heap = new MinHeap(m);
                    long start = System.nanoTime();
                    for (int x : data) {
                        heap.insert(x);
                    }
                    long end = System.nanoTime();
                    if (r >= WARMUP_RUNS) {
                        insertTimes[r - WARMUP_RUNS] = (end - start) / 1_000_000.0;
                        insMetrics = m;
                    }
                }
                Arrays.sort(insertTimes);
                double insMedian = insertTimes[insertTimes.length / 2];
                pw.printf(Locale.US, "n_inserts,%d,%.3f,%d,%d,%d%n",
                        n, insMedian, insMetrics.getSteps(), insMetrics.getMoves(), insMetrics.getComparisons());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class BenchmarkTask {
        final Metrics metrics;
        final Runnable runnable;

        BenchmarkTask(Metrics metrics, Runnable runnable) {
            this.metrics = metrics;
            this.runnable = runnable;
        }
    }

    private static void benchmarkTimedOperation(
            String workload,
            String variant,
            String structure,
            int n,
            Supplier<BenchmarkTask> taskSupplier,
            List<Result> results) {

        // 1. Warm-up runs to trigger JIT compilation without recording time
        for (int i = 0; i < WARMUP_RUNS; i++) {
            BenchmarkTask task = taskSupplier.get();
            task.metrics.reset();
            task.runnable.run();
        }

        // 2. Measurement runs
        double[] times = new double[MEASURE_RUNS];
        Metrics lastMetrics = null;

        for (int i = 0; i < MEASURE_RUNS; i++) {
            System.gc();
            BenchmarkTask task = taskSupplier.get();
            task.metrics.reset();

            long start = System.nanoTime();
            task.runnable.run();
            long end = System.nanoTime();

            times[i] = (end - start) / 1_000_000.0;
            lastMetrics = task.metrics;
        }

        Arrays.sort(times);
        double medianTime = times[times.length / 2];

        results.add(new Result(
                workload,
                variant,
                structure,
                n,
                medianTime,
                lastMetrics.getSteps(),
                lastMetrics.getMoves(),
                lastMetrics.getComparisons()));
    }
}
