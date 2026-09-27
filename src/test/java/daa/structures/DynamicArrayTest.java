package daa.structures;

import daa.metrics.Metrics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class DynamicArrayTest {
    private DynamicArray array;
    private Metrics metrics;

    @BeforeEach
    void setUp() {
        metrics = new Metrics();
        array = new DynamicArray(metrics);
    }

    @Test
    void testDifferentialCorrectnessWithRandomData() {
        ArrayList<Integer> expected = new ArrayList<>();
        Random rand = new Random(12345);

        for (int op = 0; op < 500; op++) {
            int action = rand.nextInt(4);
            if (action == 0 || expected.isEmpty()) {
                // Append
                int val = rand.nextInt(1000);
                array.add(val);
                expected.add(val);
            } else if (action == 1) {
                // Insert at index
                int idx = rand.nextInt(expected.size() + 1);
                int val = rand.nextInt(1000);
                array.add(idx, val);
                expected.add(idx, val);
            } else if (action == 2) {
                // Remove at index
                int idx = rand.nextInt(expected.size());
                array.remove(idx);
                expected.remove(idx);
            } else {
                // Get
                int idx = rand.nextInt(expected.size());
                assertEquals(expected.get(idx), array.get(idx));
            }
            assertEquals(expected.size(), array.size());
            assertEquals(expected.isEmpty(), array.isEmpty());
        }

        // Final consistency check
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i), array.get(i));
        }
    }

    @Test
    void testContains() {
        assertFalse(array.contains(10));

        array.add(10);
        array.add(20);
        array.add(30);

        assertTrue(array.contains(10));
        assertTrue(array.contains(20));
        assertTrue(array.contains(30));
        assertFalse(array.contains(40));

        array.remove(1); // remove 20
        assertFalse(array.contains(20));
        assertTrue(array.contains(10));
        assertTrue(array.contains(30));
    }

    @Test
    void testDuplicates() {
        array.add(7);
        array.add(7);
        array.add(7);
        assertEquals(3, array.size());
        assertTrue(array.contains(7));

        array.remove(0);
        assertEquals(2, array.size());
        assertEquals(7, array.get(0));
        assertEquals(7, array.get(1));
    }

    @Test
    void testResizingGrow() {
        // Initial capacity is 10; insert 100 elements to trigger grow() repeatedly
        for (int i = 0; i < 100; i++) {
            array.add(i);
        }
        assertEquals(100, array.size());
        for (int i = 0; i < 100; i++) {
            assertEquals(i, array.get(i));
        }
    }

    @Test
    void testEdgeCasesAndExceptions() {
        // Empty structure
        assertEquals(0, array.size());
        assertTrue(array.isEmpty());
        assertThrows(IndexOutOfBoundsException.class, () -> array.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> array.remove(0));

        // Invalid index bounds for add
        assertThrows(IndexOutOfBoundsException.class, () -> array.add(-1, 99));
        assertThrows(IndexOutOfBoundsException.class, () -> array.add(1, 99));

        // Single element
        array.add(42);
        assertFalse(array.isEmpty());
        assertEquals(1, array.size());
        assertEquals(42, array.get(0));

        // Boundary operations: add at head and tail
        array.add(0, 10); // [10, 42]
        array.add(2, 50); // [10, 42, 50]
        assertEquals(10, array.get(0));
        assertEquals(42, array.get(1));
        assertEquals(50, array.get(2));

        // Boundary remove: head and tail
        array.remove(0); // [42, 50]
        assertEquals(42, array.get(0));
        array.remove(1); // [42]
        assertEquals(42, array.get(0));
        assertEquals(1, array.size());

        // Remove invalid index
        assertThrows(IndexOutOfBoundsException.class, () -> array.remove(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> array.remove(1));
    }
}
