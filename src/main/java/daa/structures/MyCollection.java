package daa.structures;

public interface MyCollection {
    void add(int x);
    void add(int index, int x);
    void remove(int index);
    int get(int index);
    boolean contains(int x);
    int size();
    default boolean isEmpty() {
        return size() == 0;
    }
}
