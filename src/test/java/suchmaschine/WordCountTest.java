package suchmaschine;

import suchmaschine.WordCount;

public class WordCountTest {

    public static void main(String[] args) {
        testSetCount();
        testIncrementCount();
        testIncrementCountN();
    }

    private static void testSetCount() {
        WordCount wordCount = new WordCount("testWord", 10);

        boolean success = true;

        // testet ob methode mit "ungewollte" Werte korrekt umgeht
        wordCount.setCount(-3);
        if (wordCount.getCount() != 0) {
            System.out.println("setCount test failed. setCount did allow negative values");
            success = false;
        }

        // testet ob werte korrekt gesetzt werden
        wordCount.setCount(35);
        if (wordCount.getCount() != 35) {
            System.out.println("setCount test failed. New value was " + wordCount.getCount() + " instead of 35!");
            success = false;
        }

        WordCount wordCount1 = new WordCount("test", -123);
        if (wordCount1.getCount() != 0) {
            System.out.println("setCount test failed. Constructor allowed negative values!");
            success = false;
        }

        if (success)
            System.out.println("setCount test succeeded!");
    }

    private static void testIncrementCount() {
        WordCount wordCount = new WordCount("testWord", 11);

        // testet ob "count" korrekt erhöht wird
        int result = wordCount.incrementCount();
        if (result != 12)
            System.out.println("incrementCount test failed. Result was " + result);
        else
            System.out.println("incrementCount test succeeded!");
    }

    private static void testIncrementCountN() {
        WordCount wordCount = new WordCount("testWord", 15);

        boolean success = true;

        // testet ob die methide mit "ungewollten" Werten korrekt umgeht
        int result0 = wordCount.incrementCount(-3);
        if (result0 != 15) {
            System.out.println("incrementCountN test failed. Method did increment value though n was negative!");
            success = false;
        }

        // testet ob "count" korrekt erhöht wird
        int result1 = wordCount.incrementCount(5);
        if (result1 != 20) {
            System.out.println("incrementCountN test failed. Result was " + result1);
            success = false;
        }

        if (success)
            System.out.println("incrementCountN test succeeded!");
    }

}