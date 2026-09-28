package daa.structures;

import daa.metrics.Metrics;

public class MyLinkedList implements MyCollection {
    private static class Node {
        int val;
        Node next;
        Node prev;

        Node(int val) {
            this.val = val;
        }
    }

    private Node head;
    private Node tail;
    private int size;
    private Metrics metrics;

    public MyLinkedList(Metrics metrics) {
        this.metrics = metrics;
    }

    private Node getNode(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        Node curr;
        if (index < size / 2) {
            curr = head;
            for (int i = 0; i < index; i++) {
                metrics.addStep(); // move to next
                curr = curr.next;
            }
        } else {
            curr = tail;
            for (int i = size - 1; i > index; i--) {
                metrics.addStep(); // move to prev
                curr = curr.prev;
            }
        }
        return curr;
    }

    @Override
    public void add(int x) {
        Node newNode = new Node(x);
        if (tail == null) {
            head = tail = newNode;
            metrics.addMove(); // link head
            metrics.addMove(); // link tail
        } else {
            tail.next = newNode;
            metrics.addMove(); // link next
            newNode.prev = tail;
            metrics.addMove(); // link prev
            tail = newNode;
            metrics.addMove(); // update tail
        }
        size++;
    }

    @Override
    public void add(int index, int x) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        if (index == size) {
            add(x);
            return;
        }

        Node newNode = new Node(x);
        if (index == 0) {
            newNode.next = head;
            metrics.addMove();
            if (head != null) {
                head.prev = newNode;
                metrics.addMove();
            }
            head = newNode;
            metrics.addMove();
        } else {
            Node curr = getNode(index);
            Node prevNode = curr.prev;

            prevNode.next = newNode;
            metrics.addMove();
            newNode.prev = prevNode;
            metrics.addMove();

            newNode.next = curr;
            metrics.addMove();
            curr.prev = newNode;
            metrics.addMove();
        }
        size++;
    }

    @Override
    public void remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        Node curr = getNode(index);

        if (curr.prev != null) {
            curr.prev.next = curr.next;
            metrics.addMove();
        } else {
            head = curr.next;
            metrics.addMove();
        }

        if (curr.next != null) {
            curr.next.prev = curr.prev;
            metrics.addMove();
        } else {
            tail = curr.prev;
            metrics.addMove();
        }
        size--;
    }

    @Override
    public int get(int index) {
        Node curr = getNode(index);
        return curr.val;
    }

    @Override
    public boolean contains(int x) {
        Node curr = head;
        while (curr != null) {
            metrics.addComparison(); // compare elements
            if (curr.val == x) {
                return true;
            }
            curr = curr.next;
            metrics.addStep(); // one move to the next node
        }
        return false;
    }

    @Override
    public int size() {
        return size;
    }
}
