//*****************************************************************************
// Author: Frederic Fladenmuller
// Class: Princeton Algorithms, part 1
// Date: 9/10/18
//
// Creates a randomized queue using a resizing array. Dequeues random elements
// and iterates in random order.
//*****************************************************************************

import edu.princeton.cs.algs4.StdRandom;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class RandomizedQueue<Item> implements Iterable<Item> {
    private Item[] A;
    private int N;

    public RandomizedQueue() {
        N = 0;
        A = (Item[]) new Object[1];
    }

    public boolean isEmpty() {
        return N == 0;
    }

    public int size() {
        return N;
    }

    public void enqueue(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Null argument given to enqueue.");
        }
        if (N == A.length) {
            resize(A.length * 2);
        }

        A[N++] = item;
    }

    public Item dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException("Empty Dequeue.");
        }
        int randomIndex = StdRandom.uniform(N);
        Item item = A[randomIndex];

        // Switch last non-null element with randomly chosen element
        A[randomIndex] = A[N - 1];
        A[N - 1] = item;

        // Null last element to avoid loitering
        A[N - 1] = null;

        // Resize array if only taking up 1/4 of elements
        if (N > 0 && N == A.length / 4) {
            resize(A.length / 2);
        }
        N--;
        return item;
    }

    public Item sample() {
        if (isEmpty()) {
            throw new NoSuchElementException("Empty Dequeue.");
        }
        return A[StdRandom.uniform(N)];
    }

    private void resize(int max) {
        // Resize array, copy elements from old array to new array
        Item[] temp = (Item[]) new Object[max];
        for (int i = 0; i < N; i++) {
            temp[i] = A[i];
        }
        A = temp;
    }

    public Iterator<Item> iterator() {
        return new ListIterator();
    }

    private class ListIterator implements Iterator<Item> {
        private Item[] B;
        private int elementsInIterator;

        ListIterator() {
            B = (Item[]) new Object[N];
            elementsInIterator = N;
            for (int i = 0; i < N; i++) {
                B[i] = A[i];
            }
        }

        public boolean hasNext() {
            return elementsInIterator > 0;
        }

        private Item iteratorDequeue() {
            // The same as dequeue above, except no need to worry about resizing.
            int randomIndex = StdRandom.uniform(elementsInIterator);
            Item item = B[randomIndex];
            B[randomIndex] = B[elementsInIterator - 1];
            B[elementsInIterator - 1] = null;
            elementsInIterator--;
            return item;
        }

        public void remove() {
            throw new UnsupportedOperationException("Cannot use this function.");
        }

        public Item next() {
            if (!hasNext()) throw new NoSuchElementException("No items to return.");
            return iteratorDequeue();
        }
    }

    public static void main(String[] args) {
        // Test Client
    }
}

