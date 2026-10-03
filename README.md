# Design and Analysis of Algorithms — Assignment 2: Data Structures

**Student**: Nursayat Bashan  
**Group**: SE-2521  
**GitHub Repository**: [https://github.com/NursayatBshn/DAA-Assignment-2.git](https://github.com/NursayatBshn/DAA-Assignment-2.git) (branch `main`, tag `v1.0`)

---

## Overview
This project is an in-memory workload engine implemented in Java from scratch without using `java.util` collections. It implements:
- `DynamicArray`: primitive `int[]` storage with amortized $O(1)$ geometric growth ($2\times$).
- `MyLinkedList`: doubly linked list of primitive `int` values with bidirectional traversal optimization.
- `MinHeap`: array-based binary min-heap with $O(1)$ `peekMin`, $O(\log n)$ `extractMin` and `insert`, plus Floyd's bottom-up $O(n)$ `buildHeap`.
- Transparent metric tracking inside operations (`steps`, `moves`, `comparisons`).

All requirements, edge cases, loop invariant proofs, benchmark results, and hardware-level discussion (CPU cache locality, pointer chasing) are detailed in [REPORT.md](REPORT.md).

---

## Prerequisites
- **Java**: OpenJDK 21+
- **Maven**: 3.6+
- **Python**: 3.8+ with `matplotlib` (for generating visual plots)

---

## Build and Test
To compile the project and execute all JUnit 5 differential correctness and edge case tests:
```bash
mvn clean test
```

---

## Running Benchmarks
To run the automated benchmark across all 4 workloads ($n \in \{100, 1000, 10000, 100000\}$), the Floyd `buildHeap` comparison, and the JOL memory footprint analysis:
```bash
mvn clean compile exec:java -Dexec.mainClass="daa.Main"
```
Output files generated:
- `results/results.csv`: Workloads W1 to W4 performance metrics.
- `results/memory.csv`: JOL heap memory footprint.
- `results/buildheap_comparison.csv`: Floyd's `buildHeap` vs $n$ sequential inserts.

---

## Generating Plots
To generate PNG charts in `results/plots/`:
```bash
python3 plot.py
```
Generated plots:
- `W1_time.png` & `W1_operations.png`
- `W2_time.png` & `W2_operations.png`
- `W3_time.png` & `W3_operations.png`
- `W4_time.png` & `W4_operations.png`
- `memory_vs_n.png` (Task A)
- `buildheap_vs_insert.png` (Task B)

---

## Submission Package
Archive name format:
`DAA_Assignment2_Nursayat_Bashan_SE.zip`
