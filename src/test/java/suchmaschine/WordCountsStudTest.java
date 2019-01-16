package suchmaschine;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import suchmaschine.WordCountsArray;

import static org.junit.Assert.*;

public class WordCountsStudTest {

    private WordCountsArray wordCountsArray;

    @Before
    public void prepareArray() {
        wordCountsArray = new WordCountsArray(3);
    }

    @After
    public void cleanup() {
        wordCountsArray = null;
    }

    @org.junit.Test
    public void testNegativeWordCountsArray() {
        try {
            new WordCountsArray(-1);
        } catch (NegativeArraySizeException e) {
            fail("WordCountsArray constructor allows negative sizes");
        }
    }

    @org.junit.Test
    public void testSimpleAdd() {
        wordCountsArray.add("test1", 1);
        wordCountsArray.add("test2", 2);
        wordCountsArray.add("test3", 3);

        assertEquals("Unexpected array size after 3 adds",3, wordCountsArray.size());

        for (int i = 0; i < 3; i++) {
            int num = i+1;
            assertEquals("Unexpected word at index " + i, "test" + num, wordCountsArray.getWord(i));
            assertEquals("Unexpected count at index " + i, num, wordCountsArray.getCount(i));
        }
    }

    @org.junit.Test
    public void testArrayOverlappingAdd() {
        wordCountsArray.add("test1", 1);
        wordCountsArray.add("test2", 2);
        wordCountsArray.add("test3", 3);
        wordCountsArray.add("test4", 4);
        wordCountsArray.add("test5", 5);

        assertEquals("Unexpected size after 5 adds", 5, wordCountsArray.size());

        for (int i = 0; i < 5; i++) {
            int num = i+1;
            assertEquals("Unexpected word at index " + i, "test" + num, wordCountsArray.getWord(i));
            assertEquals("Unexpected count at index " + i, num, wordCountsArray.getCount(i));
        }
    }

    @org.junit.Test
    public void testAddWithEmptyWords() {
        wordCountsArray.add(null, 23);
        wordCountsArray.add("test1", 1);
        wordCountsArray.add("", 123);
        wordCountsArray.add("test2", 2);
        wordCountsArray.add("", 12312);
        wordCountsArray.add(null, 1239123);
        wordCountsArray.add("test3", 3);

        assertEquals("Unexpected array size after 3 adds",3, wordCountsArray.size());

        for (int i = 0; i < 3; i++) {
            int num = i+1;
            assertEquals("Unexpected word at index " + i, "test" + num, wordCountsArray.getWord(i));
            assertEquals("Unexpected count at index " + i, num, wordCountsArray.getCount(i));
        }
    }

    @org.junit.Test
    public void testAddWithNegativeCounts() {
        wordCountsArray.add("test0", -19823);
        wordCountsArray.add("test1", 1);
        wordCountsArray.add("test2", 2);
        wordCountsArray.add("test3", 3);

        assertEquals("Unexpected array size after 3 adds",3, wordCountsArray.size());

        for (int i = 0; i < 3; i++) {
            assertEquals("Unexpected word at index " + i, "test" + (i+1), wordCountsArray.getWord(i));
            assertEquals("Unexpected count at index " + i, (i + 1), wordCountsArray.getCount(i));
        }
    }

    @Test
    public void testAddWithSameWords() {
        wordCountsArray.add("test0", 5);
        wordCountsArray.add("test1", 7);
        wordCountsArray.add("test0", 1);
        wordCountsArray.add("test1", -123);

        assertEquals("Unexpected array size after adding same words", 2, wordCountsArray.size());

        for (int i = 0; i < 2; i++) {
            assertEquals("Unexpected word at index " + i, "test" + i, wordCountsArray.getWord(i));
            assertEquals("Unexpected count at index " + i, i + 6, wordCountsArray.getCount(i));
        }
    }

    @org.junit.Test
    public void testArraySize0() {
        assertEquals("Empty WordCountsArray size is not zero", 0, wordCountsArray.size());
    }

    @org.junit.Test
    public void testArraySizeAdded1() {
        wordCountsArray.add("Hello", 1);
        assertEquals("WordCountsArray size with one element is not one", 1, wordCountsArray.size());
    }

    @org.junit.Test
    public void testArraySizeAdded8() {
        for (int i = 0; i < 8; i++)
            wordCountsArray.add("" + i, i);

        assertEquals("WordCoutsArray size with eigth elements is not 8", 8, wordCountsArray.size());
    }

    @org.junit.Test
    public void testGetWord() {
        wordCountsArray.add("test1", 211);
        wordCountsArray.add("test4", 1231);

        assertEquals("Unexpected word found at index 0", "test1", wordCountsArray.getWord(0));
        assertEquals("Unexpected word found at index 1", "test4", wordCountsArray.getWord(1));

        assertNull("#getWord did not handle access to empty index correctly",
                wordCountsArray.getWord(2));

        try {
            assertNull("#getWord did not handle index out of bounds correctly",
                    wordCountsArray.getWord(18723));
        } catch (IndexOutOfBoundsException e) {
            fail("#getWord did not handle index out of bounds correctly");
        }

        try {
            assertNull("#getWord did not handle negative index correctly",
                    wordCountsArray.getWord(-12389));
        } catch (IndexOutOfBoundsException e) {
            fail("#getWord did not handle negative index correctly");
        }
    }

    @org.junit.Test
    public void testGetCount() {
        wordCountsArray.add("test1", 211);
        wordCountsArray.add("test4", 1231);

        assertEquals("Unexpected count found at index 0", 211, wordCountsArray.getCount(0));
        assertEquals("Unexpected count found at index 1", 1231, wordCountsArray.getCount(1));

        assertEquals("#getCount did not handle access to empty index correctly", -1,
                wordCountsArray.getCount(2));

        try {
            assertEquals("#getCount did not handle index out of bounds correctly", -1,
                    wordCountsArray.getCount(18723));
        } catch (IndexOutOfBoundsException e) {
            fail("#getCount did not handle index out of bounds correctly");
        }

        try {
            assertEquals("#getCount did not handle negative index correctly", -1,
                    wordCountsArray.getCount(-12389));
        } catch (IndexOutOfBoundsException e) {
            fail("#getCount did not handle negative index correctly");
        }
    }

    @Test
    public void testIndexOfWord() {
        wordCountsArray.add("test123", 1209);
        wordCountsArray.add("test9123", 1238765);
        wordCountsArray.add("test928717823", 128);

        String message0 = "Unexpected word index";
        assertEquals(message0, 0, wordCountsArray.getIndexOfWord("test123"));
        assertEquals(message0, 1, wordCountsArray.getIndexOfWord("test9123"));
        assertEquals(message0, 2, wordCountsArray.getIndexOfWord("test928717823"));

        String message1 = "Method didn't handle illegal word correctly";
        assertEquals(message1, -1, wordCountsArray.getIndexOfWord(""));
        assertEquals(message1,-1, wordCountsArray.getIndexOfWord(null));
    }

    @org.junit.Test
    public void testSetCount() {
        wordCountsArray.add("test0", 212);
        wordCountsArray.add("test1", -123);

        assertEquals("#setCount did not handle negative count correctly", -1,
                wordCountsArray.getCount(1));

        try {
            wordCountsArray.setCount(2, 1239);

            assertNotEquals("#setCount did not handle access to empty index correctly", 1239,
                    wordCountsArray.getCount(2));
        } catch (Exception e) {
            fail("#setCount did not handle access to empty index correctly");
        }

        try {
            wordCountsArray.setCount(123123, 726);
        } catch (IndexOutOfBoundsException e) {
            fail("#getCount did not handle index out of bounds correctly");
        }

        try {
            wordCountsArray.setCount(-1238, 1);
        } catch (IndexOutOfBoundsException e) {
            fail("#getCount did not handle negative index correctly");
        }
    }

    @Test
    public void testSort() {
        wordCountsArray.add("test1", 90123);
        wordCountsArray.add("test821", 9123);
        wordCountsArray.add("test0", 123);
        wordCountsArray.add("test9123812", 12);
        wordCountsArray.add("test9", 9);

        wordCountsArray.sort();

        assertEquals(0, wordCountsArray.getIndexOfWord("test0"));
        assertEquals(1, wordCountsArray.getIndexOfWord("test1"));
        assertEquals(2, wordCountsArray.getIndexOfWord("test821"));
        assertEquals(3, wordCountsArray.getIndexOfWord("test9"));
        assertEquals(4, wordCountsArray.getIndexOfWord("test9123812"));
    }

    @Test
    public void testSortComplex() {
        String example = "es war einmal eine alte geiss die hatte sieben junge geisslein und " +
                "hatte sie lieb wie eine mutter ihre kinder lieb hat a";

        for (String s: example.split(" ")) {
            wordCountsArray.add(s, 1);
        }

        wordCountsArray.sort();

        String[] result = "a alte die eine einmal es geiss geisslein hat hatte ihre junge kinder lieb mutter sie sieben und war wie".split(" ");

        assertEquals(result.length, wordCountsArray.size());

        for (int i = 0; i < result.length; i++) {
            assertEquals(i, wordCountsArray.getIndexOfWord(result[i]));
        }
    }

    @Test
    public void testSimilarityUnequal() {
        WordCountsArray wordCountsArray1 = new WordCountsArray(3);

        wordCountsArray.add("test2", 2);
        wordCountsArray1.add("test3", 2);

        assertEquals("Unexpected similarity for unequal arrays", 0,
                wordCountsArray.computeSimilarity(wordCountsArray1), 0.000000000000001);
    }

    @Test
    public void testSimilarity() {
        WordCountsArray wordCountsArray1 = new WordCountsArray(2);

        wordCountsArray.add("test123", 912);
        wordCountsArray.add("test234", 262);

        wordCountsArray1.add("test123", 126);
        wordCountsArray1.add("test234", 302);

        assertEquals("Unexpected similarity", 0.6249039686,
                wordCountsArray.computeSimilarity(wordCountsArray1), 0.0000000001);
    }

    @Test
    public void testSimilarityEmpty() {
        WordCountsArray wordCountsArray1 = new WordCountsArray(5);
        wordCountsArray1.add("test", 123);

        assertEquals(0, wordCountsArray.computeSimilarity(wordCountsArray1), 0.000000000000001);
    }

}