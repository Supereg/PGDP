package mergesort;

public class ParallelMergeSort extends Thread {

    public static void mergeSort(int[] arr, int numberOfThreadLevels) {
        ParallelMergeSort sortThread = new ParallelMergeSort(arr, 0, arr.length - 1, numberOfThreadLevels);
        sortThread.start();
        try {
            sortThread.join();
        } catch (InterruptedException e) {
            System.err.println("Couldn't sort array. Got interrupted!");
        }
    }

    private final int[] numbers;
    private final int low;
    private final int high;
    private final int numberOfThreadLevels;

    private final int partitionSize;

    public ParallelMergeSort(int[] numbers, int low, int high, int numberOfThreadLevels) {
        this.numbers = numbers;
        this.low = low;
        this.high = high;
        this.numberOfThreadLevels = numberOfThreadLevels;

        this.partitionSize = high - low + 1;
    }

    @Override
    public void run() {
        if (partitionSize == 1)
            return;
        int middle = high;

        ParallelMergeSort threadForSecondHalf = null;
        if (numberOfThreadLevels > 0 ) {
            middle = low + ((partitionSize - 1) / 2); // new middle index

            // start thread reaching from middle + 1 to high
            threadForSecondHalf = new ParallelMergeSort(numbers, middle + 1, high, numberOfThreadLevels / 2);
            threadForSecondHalf.start();
        }

        // sort our half
        sortNumbers(low, middle);

        if (threadForSecondHalf != null) { // if we starte a thread we need to merge both parts
            try {
                threadForSecondHalf.join(); // waiting that it finished
            } catch (InterruptedException e) {
                System.err.println("Interrupted while sorting in level " + numberOfThreadLevels);
            }

            // merging both groups
            mergeGroups(low, middle, high);
        }
    }

    private void sortNumbers(int low, int high) {
        int partitionSize = high - low + 1;
        if (partitionSize == 1)
            return;

        int middle = low + (partitionSize - 1) / 2;

        sortNumbers(low, middle); // sort left group
        sortNumbers(middle + 1, high); // sort right group
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