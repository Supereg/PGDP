package producerconsumer;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import static org.junit.Assert.assertTrue;

public class WorkerTest {

    private static final Random RANDOM = new Random();
    private static final Function<Integer, Integer> INC = i -> {
        randomSleep();
        return i + 1;
    };
    private static final Function<Integer, Integer> INC_2 = i -> {
        randomSleep();
        return i + 2;
    };
    private static final Function<Integer, Function<Integer, Integer>> INC_3 = i -> j -> {
        randomSleep();
        return j + 1 + i;
    };

    private static final Integer QUEUE_FIXED_LENGTH = 3;

    @Test
    public void testWorker() throws InterruptedException {
        for (int j = 0; j < 3; j++) {
            int workerLevels = RANDOM.nextInt(10) + 1;
            List<Integer> in = new ArrayList<>(QUEUE_FIXED_LENGTH);
            List<Integer> out = new ArrayList<>(QUEUE_FIXED_LENGTH);
            for (int i = 0; i < QUEUE_FIXED_LENGTH; i++) {
                Integer value = RANDOM.nextInt();
                in.add(value);
                out.add(value + workerLevels);
            }
            System.out.println("in: " + in);

            runWorker(in, out, workerLevels, i -> INC);
        }
    }

    @Test
    public void testWorker2() throws InterruptedException {
        for (int j = 0; j < 3; j++) {
            int workerLevels = RANDOM.nextInt(10) + 1;
            List<Integer> in = new ArrayList<>(QUEUE_FIXED_LENGTH);
            List<Integer> out = new ArrayList<>(QUEUE_FIXED_LENGTH);
            for (int i = 0; i < QUEUE_FIXED_LENGTH; i++) {
                Integer value = RANDOM.nextInt();
                in.add(value);
                out.add(value + 2 * workerLevels);
            }
            System.out.println("in: " + in);

            runWorker(in, out, workerLevels, i -> INC_2);
        }
    }

    @Test
    public void testWorker3() throws InterruptedException {
        for (int j = 0; j < 3; j++) {
            int workerLevels = RANDOM.nextInt(10) + 1;
            List<Integer> in = new ArrayList<>(QUEUE_FIXED_LENGTH);
            List<Integer> out = new ArrayList<>(QUEUE_FIXED_LENGTH);
            for (int i = 0; i < QUEUE_FIXED_LENGTH; i++) {
                Integer value = RANDOM.nextInt();
                in.add(value);
                out.add(value + sum(workerLevels));
            }
            System.out.println("in: " + in);

            runWorker(in, out, workerLevels, INC_3);
        }
    }

    private static int sum(int i) {
        if (i <= 1)
            return 1;
        else
            return sum(i - 1) + i;
    }

    private static void randomSleep() {
        try {
            Thread.sleep(RANDOM.nextInt(5) * 100);
        } catch (InterruptedException ignored) {}
    }

    private static void runWorker(List<Integer> in, List<Integer> expected, int workerLevels, Function<Integer, Function<Integer, Integer>> functionFactory) throws InterruptedException {
        expected = new ArrayList<>(expected);
        Queue<Integer> queue = new Queue<>();
        for (Integer i: in)
            queue.enqueue(i);

        List<Thread> workerList = new ArrayList<>(workerLevels);

        Queue<Integer> lastQueue = queue;
        for (int i = 0; i < workerLevels; i++) {
            Queue<Integer> out = new Queue<>();
            Worker<Integer, Integer> worker = new Worker<>(lastQueue, out, functionFactory.apply(i));

            workerList.add(worker);

            lastQueue = out;
        }

        for (Thread thread: workerList)
            thread.start();

        for (int i = 0; i < QUEUE_FIXED_LENGTH; i++) {
            Integer integer = lastQueue.dequeue();
            System.out.println("result: " + integer);
            expected.remove(integer);
        }

        assertTrue("Result list isn't empty. in: " + in + " -> result contained: " + expected, expected.isEmpty());
    }

}