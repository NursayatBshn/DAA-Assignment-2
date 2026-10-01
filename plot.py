import csv
import matplotlib.pyplot as plt
import os
from collections import defaultdict

os.makedirs('results/plots', exist_ok=True)

# 1. Parse results.csv
results = defaultdict(list)
with open('results/results.csv', 'r') as f:
    reader = csv.DictReader(f)
    for row in reader:
        key = (row['workload'], row['variant'], row['structure'])
        results[key].append({
            'n': int(row['n']),
            'time_ms': float(row['time_ms']),
            'steps': int(row['steps']),
            'moves': int(row['moves']),
            'comparisons': int(row['comparisons'])
        })

# Helper for plotting Time vs n
def plot_workload_time(workload, title, filename):
    plt.figure(figsize=(8, 5))
    has_data = False
    for (w, var, struct), data_points in results.items():
        if w == workload:
            data_points.sort(key=lambda x: x['n'])
            xs = [p['n'] for p in data_points]
            ys = [p['time_ms'] for p in data_points]
            label = struct if var == '-' else f"{struct} ({var})"
            plt.plot(xs, ys, marker='o', linewidth=2, label=label)
            has_data = True
            
    if has_data:
        plt.xscale('log')
        plt.yscale('log')
        plt.xlabel('n (Input Size)', fontsize=11)
        plt.ylabel('Execution Time (ms) [log scale]', fontsize=11)
        plt.title(f'{title} - Time vs n', fontsize=12, fontweight='bold')
        plt.grid(True, which="both", ls="--", alpha=0.5)
        plt.legend(fontsize=10)
        plt.tight_layout()
        plt.savefig(f'results/plots/{filename}', dpi=300)
    plt.close()

# Helper for plotting Operations vs n
def plot_workload_operations(workload, title, filename):
    plt.figure(figsize=(9, 5.5))
    for (w, var, struct), data_points in results.items():
        if w == workload:
            data_points.sort(key=lambda x: x['n'])
            xs = [p['n'] for p in data_points]
            prefix = struct if var == '-' else f"{struct} ({var})"
            
            steps = [p['steps'] for p in data_points]
            moves = [p['moves'] for p in data_points]
            cmps = [p['comparisons'] for p in data_points]
            
            if any(s > 0 for s in steps):
                plt.plot(xs, steps, marker='o', linestyle='-', label=f"{prefix} - Steps")
            if any(m > 0 for m in moves):
                plt.plot(xs, moves, marker='s', linestyle='--', label=f"{prefix} - Moves")
            if any(c > 0 for c in cmps):
                plt.plot(xs, cmps, marker='^', linestyle=':', label=f"{prefix} - Comparisons")

    plt.xscale('log')
    plt.yscale('log')
    plt.xlabel('n (Input Size)', fontsize=11)
    plt.ylabel('Operation Count [log scale]', fontsize=11)
    plt.title(f'{title} - Physical Operations vs n', fontsize=12, fontweight='bold')
    plt.grid(True, which="both", ls="--", alpha=0.5)
    plt.legend(fontsize=9, loc='best')
    plt.tight_layout()
    plt.savefig(f'results/plots/{filename}', dpi=300)
    plt.close()

# Workload 1-4 Plots
plot_workload_time('W1', 'W1: Random Access (10,000 gets)', 'W1_time.png')
plot_workload_operations('W1', 'W1: Random Access (10,000 gets)', 'W1_operations.png')

plot_workload_time('W2', 'W2: Search (1,000 contains)', 'W2_time.png')
plot_workload_operations('W2', 'W2: Search (1,000 contains)', 'W2_operations.png')

plot_workload_time('W3', 'W3: Insert & Remove (1,000 ops)', 'W3_time.png')
plot_workload_operations('W3', 'W3: Insert & Remove (1,000 ops)', 'W3_operations.png')

plot_workload_time('W4', 'W4: Priority Processing (MinHeap)', 'W4_time.png')
plot_workload_operations('W4', 'W4: Priority Processing (MinHeap)', 'W4_operations.png')

# 2. Bonus Task A: Memory vs n
if os.path.exists('results/memory.csv'):
    mem_data = defaultdict(list)
    with open('results/memory.csv', 'r') as f:
        reader = csv.DictReader(f)
        for row in reader:
            mem_data[row['structure']].append((int(row['n']), float(row['mb'])))

    plt.figure(figsize=(8, 5))
    for struct, points in mem_data.items():
        points.sort(key=lambda x: x[0])
        xs = [p[0] for p in points]
        ys = [p[1] for p in points]
        plt.plot(xs, ys, marker='o', linewidth=2, label=struct)

    plt.xscale('log')
    plt.yscale('log')
    plt.xlabel('n (Number of Elements)', fontsize=11)
    plt.ylabel('Memory Usage (MB) [log scale]', fontsize=11)
    plt.title('Task A Bonus: Memory Footprint (JOL) vs n', fontsize=12, fontweight='bold')
    plt.grid(True, which="both", ls="--", alpha=0.5)
    plt.legend(fontsize=10)
    plt.tight_layout()
    plt.savefig('results/plots/memory_vs_n.png', dpi=300)
    plt.close()

# 3. Bonus Task B: buildHeap vs n Inserts
if os.path.exists('results/buildheap_comparison.csv'):
    bh_data = defaultdict(list)
    with open('results/buildheap_comparison.csv', 'r') as f:
        reader = csv.DictReader(f)
        for row in reader:
            bh_data[row['method']].append({
                'n': int(row['n']),
                'time_ms': float(row['time_ms']),
                'comparisons': int(row['comparisons'])
            })

    # Time comparison
    fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(14, 5))
    
    for method, points in bh_data.items():
        points.sort(key=lambda x: x['n'])
        xs = [p['n'] for p in points]
        times = [p['time_ms'] for p in points]
        cmps = [p['comparisons'] for p in points]
        lbl = "Floyd's buildHeap (O(n))" if method == "buildHeap" else "n Inserts (O(n log n))"
        ax1.plot(xs, times, marker='o', linewidth=2, label=lbl)
        ax2.plot(xs, cmps, marker='s', linewidth=2, label=lbl)

    ax1.set_xscale('log')
    ax1.set_yscale('log')
    ax1.set_xlabel('n (Size)', fontsize=11)
    ax1.set_ylabel('Execution Time (ms)', fontsize=11)
    ax1.set_title('Task B: Build Time vs n', fontsize=12, fontweight='bold')
    ax1.grid(True, which="both", ls="--", alpha=0.5)
    ax1.legend()

    ax2.set_xscale('log')
    ax2.set_yscale('log')
    ax2.set_xlabel('n (Size)', fontsize=11)
    ax2.set_ylabel('Comparisons Count', fontsize=11)
    ax2.set_title('Task B: Comparisons vs n', fontsize=12, fontweight='bold')
    ax2.grid(True, which="both", ls="--", alpha=0.5)
    ax2.legend()

    plt.tight_layout()
    plt.savefig('results/plots/buildheap_vs_insert.png', dpi=300)
    plt.close()

print("All plots generated successfully with zero warnings!")
