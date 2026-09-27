package daa.structures;

import daa.metrics.Metrics;

public class DynamicArray implements MyCollection {
    private int[] data;
    private int size;
    private Metrics metrics;

    public DynamicArray(Metrics metrics) {
        this.metrics = metrics;
        this.data = new int[10];
        this.size = 0;
    }

    private void grow() {
        int newCapacity = data.length * 2;
        int[] newData = new int[newCapacity];
        for (int i = 0; i < size; i++) {
            metrics.addStep(); // read from old array
            newData[i] = data[i];
            metrics.addMove(); // write to new array
        }
        data = newData;
    }

    @Override
    public void add(int x) {
        if (size == data.length) {
            grow();
        }
        data[size++] = x;
        metrics.addMove(); // element added
    }

    @Override
    public void add(int index, int x) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        if (size == data.length) {
            grow();
        }
        for (int i = size; i > index; i--) {
            metrics.addStep(); // read
            data[i] = data[i - 1];
            metrics.addMove(); // move
        }
        data[index] = x;
        metrics.addMove(); // place element
        size++;
    }

    @Override
    public void remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        for (int i = index; i < size - 1; i++) {
            metrics.addStep(); // read
            data[i] = data[i + 1];
            metrics.addMove(); // move
        }
        size--;
    }

    @Override
    public int get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        metrics.addStep(); // read array cell
        return data[index];
    }

    @Override
    public boolean contains(int x) {
        for (int i = 0; i < size; i++) {
            metrics.addStep(); // read array cell
            metrics.addComparison(); // compare elements
            if (data[i] == x) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int size() {
        return size;
    }
}
