package daa.structures;

import daa.metrics.Metrics;

public class MinHeap {
    private int[] data;
    private int size;
    private Metrics metrics;

    public MinHeap(Metrics metrics) {
        this.metrics = metrics;
        this.data = new int[10];
        this.size = 0;
    }

    private void grow() {
        int newCapacity = data.length * 2;
        int[] newData = new int[newCapacity];
        for (int i = 0; i < size; i++) {
            metrics.addStep();
            newData[i] = data[i];
            metrics.addMove();
        }
        data = newData;
    }

    public void insert(int x) {
        if (size == data.length) {
            grow();
        }
        data[size] = x;
        metrics.addMove();
        bubbleUp(size);
        size++;
    }

    private void bubbleUp(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;
            metrics.addStep(); // read data[index]
            metrics.addStep(); // read data[parent]
            metrics.addComparison(); // compare
            if (data[index] >= data[parent]) {
                break;
            }

            // swap
            int temp = data[index];
            data[index] = data[parent];
            data[parent] = temp;
            metrics.addMove(); // write parent
            metrics.addMove(); // write index

            index = parent;
        }
    }

    public int peekMin() {
        if (size == 0) {
            throw new IllegalStateException("Heap is empty");
        }
        metrics.addStep(); // read
        return data[0];
    }

    public int extractMin() {
        if (size == 0) {
            throw new IllegalStateException("Heap is empty");
        }

        metrics.addStep(); // read
        int min = data[0];

        size--;
        if (size > 0) {
            metrics.addStep(); // read size
            data[0] = data[size];
            metrics.addMove(); // move
            bubbleDown(0);
        }
        return min;
    }

    private void bubbleDown(int index) {
        while (2 * index + 1 < size) {
            int left = 2 * index + 1;
            int right = 2 * index + 2;
            int smallest = index;

            metrics.addStep(); // read left
            metrics.addStep(); // read smallest
            metrics.addComparison(); // compare left and smallest
            if (data[left] < data[smallest]) {
                smallest = left;
            }

            if (right < size) {
                metrics.addStep(); // read right
                metrics.addStep(); // read smallest
                metrics.addComparison(); // compare right and smallest
                if (data[right] < data[smallest]) {
                    smallest = right;
                }
            }

            if (smallest == index) {
                break;
            }

            // swap
            int temp = data[index];
            data[index] = data[smallest];
            data[smallest] = temp;
            metrics.addMove();
            metrics.addMove();

            index = smallest;
        }
    }

    // Task B: O(n) buildHeap (+5%)
    public static MinHeap buildHeap(int[] array, Metrics metrics) {
        MinHeap heap = new MinHeap(metrics);
        heap.data = new int[Math.max(10, array.length)];
        for (int i = 0; i < array.length; i++) {
            metrics.addStep();
            heap.data[i] = array[i];
            metrics.addMove();
        }
        heap.size = array.length;

        for (int i = heap.size / 2 - 1; i >= 0; i--) {
            heap.bubbleDown(i);
        }
        return heap;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    boolean checkHeapProperty() {
        for (int i = 0; i < size; i++) {
            int left = 2 * i + 1;
            int right = 2 * i + 2;
            if (left < size && data[i] > data[left]) return false;
            if (right < size && data[i] > data[right]) return false;
        }
        return true;
    }
}
