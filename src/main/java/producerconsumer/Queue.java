package producerconsumer;

public class Queue<T> {

    private static final int LENGTH = 3;

    private T[] elements;
    private int start;
    private int stop;

    @SuppressWarnings("unchecked")
    public Queue() {
        this.elements = (T[]) new Object[LENGTH];
        this.start = 0;
        this.stop = -1;
    }

    private boolean isEmpty() {
        return stop == -1;
    }

    private boolean isFull() {
        return stop != -1 && (stop + 1 ) % LENGTH == start;
    }

    public synchronized void enqueue(T value) throws InterruptedException {
        while (isFull())
            wait();
        stop = (stop + 1) % LENGTH;
        elements[stop] = value;

        notifyAll();
    }

    public synchronized T dequeue() throws InterruptedException {
        while (isEmpty())
            wait();

        T element = elements[start];
        elements[start] = null; // easier for debugging
        if (start == stop) {
            stop = -1;
            start = 0;
        }
        else
            start = (start + 1) % LENGTH;

        notifyAll();
        return element;
    }

}