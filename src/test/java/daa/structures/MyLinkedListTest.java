package daa.structures;

import daa.metrics.Metrics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class MyLinkedListTest {
    private MyLinkedList list;
    private Metrics metrics;

    @BeforeEach
    void setUp() {
        metrics = new Metrics();
        list = new MyLinkedList(metrics);
    }

    @Test
    void testDifferentialCorrectnessWithRandomData() {
        LinkedList<Integer> expected = new LinkedList<>();
        Random rand = new Random(54321);

        for (int op = 0; op < 500; op++) {
            int action = rand.nextInt(4);
            if (action == 0 || expected.isEmpty()) {
                // Append
                int val = rand.nextInt(1000);
                list.add(val);
                expected.add(val);
            } else if (action == 1) {
                // Insert at index
                int idx = rand.nextInt(expected.size() + 1);
                int val = rand.nextInt(1000);
                list.add(idx, val);
                expected.add(idx, val);
            } else if (action == 2) {
                // Remove at index
                int idx = rand.nextInt(expected.size());
                list.remove(idx);
                expected.remove(idx);
            } else {
                // Get
                int idx = rand.nextInt(expected.size());
                assertEquals(expected.get(idx), list.get(idx));
            }
            assertEquals(expected.size(), list.size());
            assertEquals(expected.isEmpty(), list.isEmpty());
        }

        // Final consistency check
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i), list.get(i));
        }
    }

    @Test
    void testContains() {
        assertFalse(list.contains(10));

        list.add(10);
        list.add(20);
        list.add(30);

        assertTrue(list.contains(10));
        assertTrue(list.contains(20));
        assertTrue(list.contains(30));
        assertFalse(list.contains(40));

        list.remove(1); // remove 20
        assertFalse(list.contains(20));
        assertTrue(list.contains(10));
        assertTrue(list.contains(30));
    }

    @Test
    void testDuplicates() {
        list.add(7);
        list.add(7);
        list.add(7);
        assertEquals(3, list.size());
        assertTrue(list.contains(7));

        list.remove(0);
        assertEquals(2, list.size());
        assertEquals(7, list.get(0));
        assertEquals(7, list.get(1));
    }

    @Test
    void testBidirectionalTraversalOptimization() {
        // Test getNode index < size / 2 (from head) and index >= size / 2 (from tail)
        for (int i = 0; i < 20; i++) {
            list.add(i * 10);
        }
        assertEquals(0, list.get(0));
        assertEquals(40, list.get(4));
        assertEquals(150, list.get(15));
        assertEquals(190, list.get(19));
    }

    @Test
    void testEdgeCasesAndExceptions() {
        // Empty structure
        assertEquals(0, list.size());
        assertTrue(list.isEmpty());
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(0));

        // Invalid index bounds for add
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(-1, 99));
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(1, 99));

        // Single element
        list.add(42);
        assertFalse(list.isEmpty());
        assertEquals(1, list.size());
        assertEquals(42, list.get(0));

        // Boundary operations: add at head and tail
        list.add(0, 10); // [10, 42]
        list.add(2, 50); // [10, 42, 50]
        assertEquals(10, list.get(0));
        assertEquals(42, list.get(1));
        assertEquals(50, list.get(2));

        // Boundary remove: head and tail
        list.remove(0); // [42, 50]
        assertEquals(42, list.get(0));
        list.remove(1); // [42]
        assertEquals(42, list.get(0));
        assertEquals(1, list.size());

        // Remove invalid index
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(1));
    }
}
