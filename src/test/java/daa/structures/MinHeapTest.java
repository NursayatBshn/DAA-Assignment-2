package daa.structures;

import daa.metrics.Metrics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.PriorityQueue;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class MinHeapTest {
    private MinHeap heap;
    private Metrics metrics;

    @BeforeEach
    void setUp() {
        metrics = new Metrics();
        heap = new MinHeap(metrics);
    }

    @Test
    void testDifferentialCorrectnessWithPriorityQueue() {
        PriorityQueue<Integer> expected = new PriorityQueue<>();
        Random rand = new Random(98765);

        for (int op = 0; op < 500; op++) {
            if (expected.isEmpty() || rand.nextBoolean()) {
                int val = rand.nextInt(2000);
                heap.insert(val);
                expected.add(val);
            } else {
                assertEquals(expected.peek(), heap.peekMin());
                assertEquals(expected.poll(), heap.extractMin());
            }
            assertEquals(expected.size(), heap.size());
            assertEquals(expected.isEmpty(), heap.isEmpty());
            assertTrue(heap.checkHeapProperty());
        }

        // Drain remaining
        while (!expected.isEmpty()) {
            assertEquals(expected.peek(), heap.peekMin());
            assertEquals(expected.poll(), heap.extractMin());
            assertTrue(heap.checkHeapProperty());
        }
        assertTrue(heap.isEmpty());
    }

    @Test
    void testHeapPropertyAfterEveryOperation() {
        Random rand = new Random(42);
        // Insert 200 elements, check heap property after each insert
        for (int i = 0; i < 200; i++) {
            heap.insert(rand.nextInt(1000));
            assertTrue(heap.checkHeapProperty(), "Heap property violated after insert");
        }

        assertEquals(200, heap.size());

        // Extract all elements, check heap property after each extraction
        int prev = heap.extractMin();
        assertTrue(heap.checkHeapProperty(), "Heap property violated after extractMin");
        while (!heap.isEmpty()) {
            int current = heap.extractMin();
            assertTrue(prev <= current, "Sorted order violated: " + prev + " > " + current);
            assertTrue(heap.checkHeapProperty(), "Heap property violated after extractMin");
            prev = current;
        }
        assertEquals(0, heap.size());
    }

    @Test
    void testResizingGrow() {
        // Initial capacity is 10; insert 150 elements to trigger grow() several times
        for (int i = 150; i >= 1; i--) {
            heap.insert(i);
        }
        assertEquals(150, heap.size());
        assertTrue(heap.checkHeapProperty());

        for (int i = 1; i <= 150; i++) {
            assertEquals(i, heap.extractMin());
        }
        assertTrue(heap.isEmpty());
    }

    @Test
    void testDuplicates() {
        heap.insert(15);
        heap.insert(15);
        heap.insert(15);
        heap.insert(5);
        heap.insert(5);

        assertEquals(5, heap.size());
        assertEquals(5, heap.extractMin());
        assertEquals(5, heap.extractMin());
        assertEquals(15, heap.extractMin());
        assertEquals(15, heap.extractMin());
        assertEquals(15, heap.extractMin());
        assertTrue(heap.isEmpty());
    }

    @Test
    void testEdgeCasesAndExceptions() {
        // Empty heap
        assertEquals(0, heap.size());
        assertTrue(heap.isEmpty());
        assertThrows(IllegalStateException.class, () -> heap.peekMin());
        assertThrows(IllegalStateException.class, () -> heap.extractMin());

        // Single element
        heap.insert(42);
        assertFalse(heap.isEmpty());
        assertEquals(1, heap.size());
        assertEquals(42, heap.peekMin());
        assertEquals(42, heap.extractMin());
        assertTrue(heap.isEmpty());
    }

    @Test
    void testBuildHeapFloydVsPriorityQueue() {
        int[] array = {25, 12, 90, 4, 33, 17, 8, 55, 6, 2, 77, 1};
        PriorityQueue<Integer> pq = new PriorityQueue<>();
        for (int v : array) pq.add(v);

        MinHeap builtHeap = MinHeap.buildHeap(array, metrics);
        assertTrue(builtHeap.checkHeapProperty());
        assertEquals(array.length, builtHeap.size());

        while (!pq.isEmpty()) {
            assertEquals(pq.poll(), builtHeap.extractMin());
            assertTrue(builtHeap.checkHeapProperty());
        }
        assertTrue(builtHeap.isEmpty());
    }
}
