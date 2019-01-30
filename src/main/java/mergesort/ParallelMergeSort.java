package mergesort;

public class ParallelMergeSort extends Thread {

    public static void mergeSort(int[] arr, int numberOfThreadLevels) {
        ParallelMergeSort sortThread = new ParallelMergeSort(arr, 0, arr.length - 1, numberOfThreadLevels);
        sortThread.run();
    }

    private final int[] numbers;
    private final int low;
    private final int high;
    private final int numberOfThreadLevels;

    public ParallelMergeSort(int[] numbers, int low, int high, int numberOfThreadLevels) {
        this.numbers = numbers;
        this.low = low;
        this.high = high;
        this.numberOfThreadLevels = numberOfThreadLevels;
    }

    @Override
    public void run() {
        sortParallel(low, high, numberOfThreadLevels);
    }

    private void sortParallel(int low, int high, int numberOfThreadLevels) {
        int partitionSize = high - low + 1;
        if (partitionSize == 1)
            return;

        if (numberOfThreadLevels > 0) {
            numberOfThreadLevels /= 2; // cut thread level in half
            int middle = low + ((partitionSize -1 ) / 2); // new middle index;

            // start thread reaching from low to middle
            ParallelMergeSort threadForSecondHalf = new ParallelMergeSort(numbers, low, middle, numberOfThreadLevels);
            threadForSecondHalf.start();

            // sort other part recursively in parallel
            sortParallel(middle + 1, high, numberOfThreadLevels);

            try {
                threadForSecondHalf.join(); // wait for the thread to finish
            } catch (InterruptedException e) {
                System.err.println("Interrupted while trying to merge " + this.getName() + " and " + threadForSecondHalf.getName());
            }
            // merge both parts
            mergeGroups(low, middle, high);
        }
        else {
            sort(low, high);
        }
    }

    private void sort(int low, int high) {
        int partitionSize = high - low + 1;
        if (partitionSize == 1)
            return;

        int middle = low + (partitionSize - 1) / 2;

        sort(low, middle); // sort left group
        sort(middle + 1, high); // sort right group
        mergeGroups(low, middle, high); // merge both groups
    }

    private void mergeGroups(int low, int middle, int high) {
        int[] sorted = new int[high - low + 1];

        int groupAPointer = low;
        int groupBPointer = middle + 1;

        for (int i = 0; i < sorted.length; i++) {
            if (groupAPointer > middle) // a is empty
                sorted[i] = numbers[groupBPointer++];
            else if (groupBPointer > high) // b is empty
                sorted[i] = numbers[groupAPointer++];
            else if (numbers[groupAPointer] < numbers[groupBPointer]) // a < b
                sorted[i] = numbers[groupAPointer++];
            else // b >= a
                sorted[i] = numbers[groupBPointer++];
        }

        System.arraycopy(sorted, 0, numbers, low, sorted.length);
    }

}