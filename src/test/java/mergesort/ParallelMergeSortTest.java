package mergesort;

import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

import static org.junit.Assert.assertArrayEquals;

public class ParallelMergeSortTest {

    @Test
    public void testMergeSortMulti() {
        int[] ints = new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 13, 16, 27, 1000000};
        for (int n : ints) {
            System.out.println("ParallelMergeSort for length=" + n);
            mergeSort(n);
            System.out.println();
            System.out.println();
        }
    }

    public void mergeSort(int n) {
        // Zufälliges großes Array zum Testen
        int maxValue = 10000000;
        Random rand = new Random();

        int[] randomArray = new int[n];
        for (int i = 0; i < n; i++) {
            randomArray[i] = rand.nextInt(maxValue);
        }
        int[] sortedArray = Arrays.copyOf(randomArray, randomArray.length);
        Arrays.sort(sortedArray);

        int[] copy1 = Arrays.copyOf(randomArray, randomArray.length);

        long timeStart = System.nanoTime();
        ParallelMergeSort.mergeSort(copy1, 0);
        long timeEnd = System.nanoTime();
        long timeDiff = timeEnd - timeStart;
        // System.out.println(Arrays.toString(copy1));
        assertArrayEquals("Single-Threaded MergeSort - Das Array sollte sortiert sein!", sortedArray, copy1);
        System.out.println("Single-Threaded MergeSort MergeSort took: " + timeDiff + " nanoseconds.");

        copy1 = Arrays.copyOf(randomArray, randomArray.length);
        timeStart = System.nanoTime();
        ParallelMergeSort.mergeSort(copy1, 4);
        timeEnd = System.nanoTime();
        timeDiff = timeEnd - timeStart;
        assertArrayEquals("ParallelMergeSort - n = 4 - Das Array sollte sortiert sein!", sortedArray, copy1);
        System.out.println("Parallel MergeSort with n =   4 took: " + timeDiff + " nanoseconds.");

        // n = 8
        copy1 = Arrays.copyOf(randomArray, randomArray.length);
        timeStart = System.nanoTime();
        ParallelMergeSort.mergeSort(copy1, 8);
        timeEnd = System.nanoTime();
        timeDiff = timeEnd - timeStart;
        assertArrayEquals("ParallelMergeSort - n = 8 - Das Array sollte sortiert sein!", sortedArray, copy1);
        System.out.println("Parallel MergeSort with n =   8 took: " + timeDiff + " nanoseconds.");

        // n = 16
        copy1 = Arrays.copyOf(randomArray, randomArray.length);
        timeStart = System.nanoTime();
        ParallelMergeSort.mergeSort(copy1, 16);
        timeEnd = System.nanoTime();
        timeDiff = timeEnd - timeStart;
        assertArrayEquals("ParallelMergeSort - n = 16 - Das Array sollte sortiert sein!", sortedArray, copy1);
        System.out.println("Parallel MergeSort with n =  16 took: " + timeDiff + " nanoseconds.");

        // n = 32
        copy1 = Arrays.copyOf(randomArray, randomArray.length);
        timeStart = System.nanoTime();
        ParallelMergeSort.mergeSort(copy1, 32);
        timeEnd = System.nanoTime();
        timeDiff = timeEnd - timeStart;
        assertArrayEquals("ParallelMergeSort - n = 32 - Das Array sollte sortiert sein!", sortedArray, copy1);
        System.out.println("Parallel MergeSort with n =  32 took: " + timeDiff + " nanoseconds.");

        // n = 128
        copy1 = Arrays.copyOf(randomArray, randomArray.length);
        timeStart = System.nanoTime();
        ParallelMergeSort.mergeSort(copy1, 128);
        timeEnd = System.nanoTime();
        timeDiff = timeEnd - timeStart;
        assertArrayEquals("ParallelMergeSort - n = 128 - Das Array sollte sortiert sein!", sortedArray, copy1);
        System.out.println("Parallel MergeSort with n = 128 took: " + timeDiff + " nanoseconds.");
    }


}
