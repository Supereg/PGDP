package utils;

import mergersort.MergeSort;
import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

import static org.junit.Assert.assertArrayEquals;

public class MergeSortTest {

  @Test
  public void testMergeSort() {
    Random rand = new Random(42);
    int[] numbers = new int[1000000];
    for(int i = 0; i < numbers.length; i++)
      numbers[i] = rand.nextInt();
    int[] numbersSorted = MergeSort.mergeSortIt(numbers);
    Arrays.sort(numbers);
    assertArrayEquals("Feld nicht korrekt sortiert.", numbers, numbersSorted);
    
    // assertTrue("Bitte prüfen, dass wirklich ein iteratives MergeSort implementiert wird!", false);
  }

}
