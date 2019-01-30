package producerconsumer;

import java.util.function.Function;

public class Worker<T, U> extends Thread {

    private Queue<T> consumerQueue;
    private Queue<U> producerQueue;

    private Function<T, U> workerFunction;

    public Worker(Queue<T> consumerQueue, Queue<U> producerQueue, Function<T, U> workerFunction) {
        this.consumerQueue = consumerQueue;
        this.producerQueue = producerQueue;
        this.workerFunction = workerFunction;
    }

    @Override
    public void run() {
        try {
            //noinspection InfiniteLoopStatement
            for (;;) {
                T element = consumerQueue.dequeue();
                U transformed = workerFunction.apply(element);
                producerQueue.enqueue(transformed);
            }
        } catch (InterruptedException ignored) {}
    }

}