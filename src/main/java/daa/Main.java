package daa;

import daa.bench.Benchmark;
import daa.bench.CsvWriter;
import daa.bench.Result;
import org.openjdk.jol.info.GraphLayout;
import daa.structures.DynamicArray;
import daa.structures.MyLinkedList;
import daa.structures.MinHeap;
import daa.metrics.Metrics;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.Locale;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting DAA Assignment 2 Benchmark...");
        
        File dir = new File("results");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        List<Result> results = Benchmark.runAll();
        CsvWriter.writeResults("results/results.csv", results);
        
        System.out.println("Benchmark finished. Results saved to results/results.csv.");

        System.out.println("\n--- Task A: Memory Footprint (JOL) ---");
        int[] memorySizes = {100, 1000, 10000, 100000};
        
        try (PrintWriter pw = new PrintWriter(new FileWriter("results/memory.csv"))) {
            pw.println("structure,n,bytes,mb");

            for (int n : memorySizes) {
                Metrics m = new Metrics();

                DynamicArray arr = new DynamicArray(m);
                for (int i = 0; i < n; i++) arr.add(i);
                long arrBytes = GraphLayout.parseInstance(arr).totalSize();
                double arrMb = arrBytes / (1024.0 * 1024.0);
                pw.printf(Locale.US, "DynamicArray,%d,%d,%.4f%n", n, arrBytes, arrMb);
                System.out.printf("DynamicArray (n=%d): %d bytes (%.4f MB)%n", n, arrBytes, arrMb);

                MyLinkedList list = new MyLinkedList(m);
                for (int i = 0; i < n; i++) list.add(i);
                long listBytes = GraphLayout.parseInstance(list).totalSize();
                double listMb = listBytes / (1024.0 * 1024.0);
                pw.printf(Locale.US, "MyLinkedList,%d,%d,%.4f%n", n, listBytes, listMb);
                System.out.printf("MyLinkedList (n=%d): %d bytes (%.4f MB)%n", n, listBytes, listMb);

                MinHeap heap = new MinHeap(m);
                for (int i = 0; i < n; i++) heap.insert(i);
                long heapBytes = GraphLayout.parseInstance(heap).totalSize();
                double heapMb = heapBytes / (1024.0 * 1024.0);
                pw.printf(Locale.US, "MinHeap,%d,%d,%.4f%n", n, heapBytes, heapMb);
                System.out.printf("MinHeap (n=%d): %d bytes (%.4f MB)%n", n, heapBytes, heapMb);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Memory results saved to results/memory.csv.");
    }
}
