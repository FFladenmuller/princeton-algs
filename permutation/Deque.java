//********************************************
// Author: Frederic Fladenmuller
// Class: Princeton Algorithms, part 1
// Date: 9/10/18
//
// This program uses a doubly linked list in order to have a queue
// or dequeue where you can add and remove from both the front and
// back of the queue.
//*********************************************


import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<Item> implements Iterable<Item> {
    private int n;
    private Node first;
    private Node last;

    private class Node {
        private Item item;
        private Node next;
        private Node previous;
    }

    public Deque() {
        first = null;
        last = null;
    }

    public boolean isEmpty() {
        return n == 0;
    }

    public int size() {
        return n;
    }

    // Add item to front of deque
    public void addFirst(Item item) {
        // Add Node to front of Dequeue
        if (item == null) {
            throw new IllegalArgumentException("Null argument given to addFirst.");
        }
        Node oldFirst = first;
        first = new Node();
        first.item = item;
        first.next = oldFirst;
        first.previous = null;
        n++;
        if (n == 1) {
            last = first;
        }
        if (n > 1) {
            first.next.previous = first;
        }
    }

    // Add item to back of deque
    public void addLast(Item item) {
        // Add Node to end of Dequeue
        if (item == null) {
            throw new IllegalArgumentException("Null argument given to addLast.");
        }
        Node oldLast = last;
        last = new Node();
        last.item = item;
        last.next = null;
        last.previous = oldLast;
        if (isEmpty()) {
            first = last;
        } else {
            oldLast.next = last;
        }
        n++;
    }

    // Remove item from front of deque
    public Item removeFirst() {
        if (isEmpty()) {
            throw new NoSuchElementException("Empty Dequeue.");
        }

        Item item = first.item;
        if (--n > 0) {
            first.next.previous = null;
        } else {
            last = null;
        }
        first = first.next;
        return item;
    }

    // Remove item from back of deque
    public Item removeLast() {
        if (isEmpty()) {
            throw new NoSuchElementException("Empty Dequeue.");
        }
        Item item = last.item;
        last = last.previous;
        if (last != null) {
            last.next = null;
        }
        if (--n == 0) {
            first = null;
        }
        return item;
    }

    public Iterator<Item> iterator() {
        return new ListIterator();
    }

    private class ListIterator implements Iterator<Item> {
        private Node current = first;

        public boolean hasNext() {
            return current != null;
        }

        public void remove() {
            throw new UnsupportedOperationException("Cannot use this function.");
        }

        public Item next() {
            if (!hasNext()) throw new NoSuchElementException("No items to return.");

            Item item = current.item;
            current = current.next;
            return item;
        }

    }

    public static void main(String[] args) {
        // Test Client
    }
}
