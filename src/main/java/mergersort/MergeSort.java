package mergersort;

import java.util.Arrays;

public class MergeSort {

    @SuppressWarnings("Duplicates")
    public static int[] mergeSortIt(int[] a) {
        for (int groupSize = 1; groupSize <= a.length; groupSize *= 2) {
            int[] aCopy = new int[a.length];
            int insertIndex = 0;

            for (int startOfTwoGroups = 0; startOfTwoGroups < a.length; startOfTwoGroups += (2* groupSize)) {
                int groupA = startOfTwoGroups;
                int groupB = startOfTwoGroups + groupSize;

                int groupAEnd = min(a.length, groupB);
                int groupBEnd = min(a.length, startOfTwoGroups + groupSize*2);

                int endInsertIndex = min(startOfTwoGroups + (2* groupSize), a.length);

                for (; insertIndex < endInsertIndex; insertIndex++) {
                    if (groupA < groupAEnd && groupB < groupBEnd) {
                        aCopy[insertIndex] = a[groupA] <= a[groupB]
                                ? a[groupA++]
                                : a[groupB++];
                    }
                    else if (groupA >= groupAEnd && groupB < groupBEnd)
                        aCopy[insertIndex] = a[groupB++];
                    else if (groupB >= groupBEnd && groupA < groupAEnd)
                        aCopy[insertIndex] = a[groupA++];
                    else {
                        System.err.println("Index " + insertIndex + "/" + endInsertIndex
                                + " is unassigned for groupSize: " + groupSize + " and startOfTwoGroups: "
                                + startOfTwoGroups + " (A: " + groupA + " B: " + groupB + ")");
                    }
                }
            }

            a = aCopy;
        }

        return a;
    }

    private static int min(int a, int b) { // our own equivalent of Math.min
        return a <= b? a: b;
    }

    public static void main(String[] args) {
        test(new int[] {5, 3, 8, 4, 3, 6, 2}, new int[] {2, 3, 3, 4, 5, 6, 8}, 1);
        test(new int[] {4, 12 , 6, 123, 56, 3, 1, 2, 5, 6, 211}, new int[] {1, 2, 3, 4, 5, 6, 6, 12, 56, 123, 211}, 2);
        test(new int[] {3, 7, 1234, 78, 13, 5, 7, 2, 3, 8, 5, 1, 3, 8, 0},
                new int[] {0, 1, 2, 3, 3, 3, 5, 5, 7, 7, 8, 8, 13, 78, 1234}, 3);
        test(new int[0], new int[0], 4);
        test(new int[] {1}, new int[] {1}, 5);
    }

    private static void test(int[] in, int[] expected, int testNum) {
        int[] result = mergeSortIt(in);
        if (!Arrays.equals(result, expected))
            System.out.println("test" + testNum + " failed");
    }

}