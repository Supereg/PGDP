package utils;

import utils.MatrixVectorOperations;

import java.util.Arrays;

import static org.junit.Assert.*;

public class MatricesTest {

    /*
     * Aufgabe 5.8
     */

    // 0.25P
    @org.junit.Test
    public void testMultiply() throws Exception {
        double[][] matrix;
        double[] vector, actual, expected;

        matrix = new double[][] { { 0.5, 1.6, 0.0 }, { 1.5, 4.0, 1.0 }, { 0.3, 7.2, 2.0 } };
        vector = new double[] { 1.6, 4.0, 7.2 };
        expected = new double[] { 7.2, 25.6, 43.68 };
        actual = MatrixVectorOperations.multiply(matrix, vector);
        for (int i = 0; i < expected.length; i++)
            assertEquals(expected[i], actual[i], 0.00001);

        matrix = new double[][] { { 0.5, 1.5 }, { 1.6, 4.0 } };
        vector = new double[] { 1, 0 };
        expected = new double[] { 0.5, 1.6 };
        actual = MatrixVectorOperations.multiply(matrix, vector);
        for (int i = 0; i < expected.length; i++)
            assertEquals(expected[i], actual[i], 0.0);
    }

    // 0.25P
    @org.junit.Test
    public void testCosineSimilarity() throws Exception {
        double[] vector1, vector2;
        double actual, expected;

        vector1 = new double[] { 0.5, 4.0, 0.3, 2.0 };
        vector2 = new double[] { 0.1, 2.0, 0.34, 5.6 };
        expected = 0.720318061;
        actual = MatrixVectorOperations.cosineSimilarity(vector1, vector2);
        assertEquals(expected, actual, 0.000001);

        vector1 = new double[] { 0.01, 0.01, 0.01 };
        vector2 = new double[] { 0.01, 0.01, 0.01 };
        expected = 1.0;
        actual = MatrixVectorOperations.cosineSimilarity(vector1, vector2);
        assertEquals(expected, actual, 0.000001);
    }

    // 0.25P
    @org.junit.Test
    public void testTranspose() throws Exception {
        double[][] original, expected, actual;

        original = new double[][] { { 0.5, 1.6, 0.0 }, { 1.5, 4.0, 1.0 }, { 0.3, 7.2, 2.0 } };
        expected = new double[][] { { 0.5, 1.5, 0.3 }, { 1.6, 4.0, 7.2 }, { 0.0, 1.0, 2.0 } };
        actual = MatrixVectorOperations.transpose(original);
        for (int i = 0; i < expected.length; i++)
            for (int j = 0; j < expected[0].length; j++)
                assertEquals(expected[i][j], actual[i][j], 0.0);

        original = new double[][] { { 0.5, 1.6, 0.0 }, { 1.5, 4.0, 1.0 } };
        expected = new double[][] { { 0.5, 1.5 }, { 1.6, 4.0 }, { 0.0, 1.0 } };
        actual = MatrixVectorOperations.transpose(original);
        for (int i = 0; i < expected.length; i++)
            for (int j = 0; j < expected[0].length; j++)
                assertEquals(expected[i][j], actual[i][j], 0.0);
    }

    // 0.25P
    @org.junit.Test
    public void testEuclideanDistance() throws Exception {
        double[] vector1, vector2;
        double actual, expected;

        vector1 = new double[] { 0.5, 4.0, 0.3, 2.0 };
        vector2 = new double[] { 0.1, 2.0, 0.34, 5.6 };
        expected = 4.137825515;
        actual = MatrixVectorOperations.euclideanDistance(vector1, vector2);
        assertEquals(expected, actual, 0.000001);

        vector1 = new double[] { 0.01, 0.01, 0.01 };
        vector2 = new double[] { 0.01, 0.01, 0.01 };
        expected = 0.0;
        actual = MatrixVectorOperations.euclideanDistance(vector1, vector2);
        assertEquals(expected, actual, 0);
    }

    // Falls jemand seltsam rechnet und Rundungsfehler auftreten
//  private void assertAlmostEquals(String msg, double actual, double expected) {
//    double diff = Math.abs(actual - expected);
//    if (diff > 0.00001)
//      assertEquals(msg, actual, expected);
//  }

//  @org.junit.Test
//  public void dotProduct() throws Exception {
//    double[] vector1, vector2;
//    double actual, expected;
//    String msg = "Skalarprodukt ist fehlerhaft.";
//
//    vector1 = new double[] { 1, 2, 3 };
//    vector2 = new double[] { 4, 5, 6 };
//    expected = 32;
//    actual = MatrixVectorOperations.dotProduct(vector1, vector2);
//    assertEquals(expected, actual, 0.000001);
//
//    vector1 = new double[] { 0.1, 0.2, 0.3 };
//    vector2 = new double[] { 0.4, 0.5, 0.6 };
//    expected = 0.32;
//    actual = MatrixVectorOperations.dotProduct(vector1, vector2);
//    assertEquals(expected, actual, 0.000001);
//  }

    /*
     * Aufgabe 5.9
     */

    private static boolean isEqual(int[] row1, int[] row2) {
        for (int i = 0; i < row1.length; i++)
            if (row1[i] != row2[i])
                return false;
        return true;
    }

    private static boolean contains(int[][] haystack, int[] needle) {
        for (int i = 0; i < haystack.length; i++)
            if (isEqual(haystack[i], needle))
                return true;
        return false;
    }

    private static boolean isEqual(int[][] permutations1, int[][] permutations2) {
        for (int i = 0; i < permutations2.length; i++)
            if (!contains(permutations1, permutations2[i]))
                return false;
        return true;
    }

    private static int fak(int n) {
        if (n <= 1)
            return n;
        return n * fak(n - 1);
    }

    // 1.5P
    @org.junit.Test
    public void testPermutationsSmall() throws Exception {
        int[][] permutations, expected;
        String msg = "at least one permutation is missing.";

        permutations = MatrixVectorOperations.permutations(1);
        expected = new int[][] { { 0 }, { 0 } };
        assertTrue(msg, isEqual(permutations, expected));

        permutations = MatrixVectorOperations.permutations(2);
        expected = new int[][] { { 0, 1 }, { 1, 0 } };
        assertTrue(msg, isEqual(permutations, expected));

        permutations = MatrixVectorOperations.permutations(3);
        expected = new int[][] { { 0, 1, 2 }, { 1, 0, 2 }, { 1, 2, 0 }, { 0, 2, 1 }, { 2, 0, 1 }, { 2, 1, 0 } };
        assertTrue(msg, isEqual(permutations, expected));

        // Todo: REVISE! (Wichtig: Reihenfolge mag bei Studis anders sein!)
        permutations = MatrixVectorOperations.permutations(4);
        expected = new int[][] { { 0, 1, 2, 3 }, { 0, 1, 3, 2 }, { 0, 2, 1, 3 }, { 0, 2, 3, 1 }, { 0, 3, 1, 2 },
                { 0, 3, 2, 1 }, { 1, 0, 2, 3 }, { 1, 0, 3, 2 }, { 1, 2, 0, 3 }, { 1, 2, 3, 0 }, { 1, 3, 0, 2 }, { 1, 3, 2, 0 },
                { 2, 0, 1, 3 }, { 2, 0, 3, 1 }, { 2, 1, 0, 3 }, { 2, 1, 3, 0 }, { 2, 3, 0, 1 }, { 2, 3, 1, 0 }, { 3, 0, 1, 2 },
                { 3, 0, 2, 1 }, { 3, 1, 0, 2 }, { 3, 1, 2, 0 }, { 3, 2, 0, 1 }, { 3, 2, 1, 0 } };
        assertTrue(msg, isEqual(permutations, expected));

        // assertTrue("Bitte prüfen, dass die Permutationen nicht explizit im Code stehen!", false);
    }

    // 1.5P
    @org.junit.Test
    public void testPermutationsLarge() throws Exception {
        for (int n = 5; n <= 10; n++) {
            int[][] permutations = MatrixVectorOperations.permutations(n);

            // There are n! many permutations.
            assertEquals("Invalid number of permutations", fak(n), permutations.length);

            // Each permutation contains n elements
            for (int i = 0; i < permutations.length; i++)
                assertEquals(n, permutations[i].length);

            // Each permutation consists of the number from 0 through n - 1
            for (int i = 0; i < permutations.length; i++) {
                boolean[] hit = new boolean[n];
                for (int j = 0; j < n; j++) {
                    int v = permutations[i][j];
                    assertTrue("Invalid permutation element", v >= 0);
                    assertTrue("Invalid permutation element", v < n);
                    hit[v] = true;
                }
                for (int j = 0; j < n; j++)
                    assertTrue("Number missing in permutation", hit[j]);
            }

            // All permutations are distinct
            Arrays.sort(permutations, (int[] a, int[] b) -> {
                for (int i = 0; i < a.length; i++) {
                    int cmp = Integer.compare(a[i], b[i]);
                    if (cmp < 0)
                        return cmp;
                    else if (cmp > 0)
                        return cmp;
                }
                return 0;
            });
            for (int i = 1; i < permutations.length; i++) {
                assertFalse("Permutations not distinct", Arrays.equals(permutations[i - 1], permutations[i]));
            }
        }
    }

    // 1.5P
    @org.junit.Test
    public void testSgn() throws Exception {
        int[][] permutations = { { 1 }, { 2, 1 }, { 1, 3, 2 }, { 3, 1, 2, 4 }, { 2, 1, 5, 4, 3 } };
        int[] parity = { 1, -1, -1, 1, 1 };

        for (int i = 0, sgn = 0; i < permutations.length; i++) {
            sgn = MatrixVectorOperations.sgn(permutations[i]);
            assertEquals(parity[i], sgn);
        }

        permutations = new int[][] { { 0, 1, 2, 3 }, { 0, 1, 3, 2 }, { 0, 2, 1, 3 }, { 0, 2, 3, 1 }, { 0, 3, 1, 2 },
                { 0, 3, 2, 1 }, { 1, 0, 2, 3 }, { 1, 0, 3, 2 }, { 1, 2, 0, 3 }, { 1, 2, 3, 0 }, { 1, 3, 0, 2 }, { 1, 3, 2, 0 },
                { 2, 0, 1, 3 }, { 2, 0, 3, 1 }, { 2, 1, 0, 3 }, { 2, 1, 3, 0 }, { 2, 3, 0, 1 }, { 2, 3, 1, 0 }, { 3, 0, 1, 2 },
                { 3, 0, 2, 1 }, { 3, 1, 0, 2 }, { 3, 1, 2, 0 }, { 3, 2, 0, 1 }, { 3, 2, 1, 0 } };
        parity = new int[] { 1, -1, -1, 1, 1, -1, -1, 1, 1, -1, -1, 1, 1, -1, -1, 1, 1, -1, -1, 1, 1, -1, -1, 1 };

        for (int i = 0, sgn = 0; i < permutations.length; i++) {
            sgn = MatrixVectorOperations.sgn(permutations[i]);
            assertEquals(parity[i], sgn);
        }
    }

    // 1.5P
    @org.junit.Test
    public void testDeterminant() throws Exception {
        String msg = "Determinante falsch berechnet.";
        int[][] matrix;
        int expectedResult;
        int actualResult;

        matrix = new int[][] { { 3, 7, 9 }, { 1, -3, 5 }, { -8, 0, -2 } };
        expectedResult = -464;
        actualResult = MatrixVectorOperations.determinant(matrix);
        assertEquals(msg, expectedResult, actualResult);

        matrix = new int[][] { { 0, 0, 0 }, { 0, 0, 0 }, { 0, 0, 0 } };
        expectedResult = 0;
        actualResult = MatrixVectorOperations.determinant(matrix);
        assertEquals(msg, expectedResult, actualResult);

        matrix = new int[][] { { -1, -1 }, { -1, -1 } };
        expectedResult = 0;
        actualResult = MatrixVectorOperations.determinant(matrix);
        assertEquals(msg, expectedResult, actualResult);

        matrix = new int[][] { { 1, 1 }, { 1, 1 } };
        expectedResult = 0;
        actualResult = MatrixVectorOperations.determinant(matrix);
        assertEquals(msg, expectedResult, actualResult);

        matrix = new int[][] { { 0, 1, 2 }, { 3, 4, 5 }, { 6, 7, 8 } };
        expectedResult = 0;
        actualResult = MatrixVectorOperations.determinant(matrix);
        assertEquals(msg, expectedResult, actualResult);

        matrix = new int[][] { { 0, 1, 2, 3 }, { 4, 5, 6, 7 }, { 8, 9, 10, 11 }, { 12, 13, 14, 15 } };
        expectedResult = 0;
        actualResult = MatrixVectorOperations.determinant(matrix);
        assertEquals(msg, expectedResult, actualResult);

        matrix = new int[][] { { -1, 1, 2, 3 }, { 4, 5, 6, 7 }, { 8, 9, 10, 11 }, { 12, 13, 14, 15 } };
        expectedResult = 0;
        actualResult = MatrixVectorOperations.determinant(matrix);
        assertEquals(msg, expectedResult, actualResult);

        matrix = new int[][] { { -1, 1, 2, 3 }, { 4, -5, 6, 7 }, { 8, 9, 10, 11 }, { 12, 13, 99, 15 } };
        expectedResult = -30470;
        actualResult = MatrixVectorOperations.determinant(matrix);
        assertEquals(msg, expectedResult, actualResult);
    }
}
