import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Andi on 19.11.18.
 */
public class MatricesStudTest {

    private static final double DELTA = 0.000000000000001;

    @Test
    public void testMultiply() {
        double[][] matrix = {{1, 2, 0}, {0, 1, 1}, {0, 1, -1}};
        double[] vector = {3, -1, 1};

        double[] result = MatrixVectorOperations.multiply(matrix, vector);
        assertNotNull("Result was unexpectedly null", result);
        assertArrayEquals("Unexpected result for matrix multiply", new double[] {1, 0, -2}, result, DELTA);
    }

    @Test
    public void testCosineSimilarity() {
        double[] vector0 = {1, 5, 3, -2, -7, 3};
        double[] vector1 = {-6, 2, 8, 2, 1, -23};

        double result = MatrixVectorOperations.cosineSimilarity(vector0, vector1);
        assertEquals("Unexpected result for cosine similarity", -1, result, DELTA);

        assertEquals(result, MatrixVectorOperations.cosineSimilarity(vector1, vector0), DELTA);
    }

    @Test
    public void testTranspose() {
        double[][] matrix = {
                {0, 1, 5, 23, 1},
                {3, 234, 12, 5, 3},
                {102, 234, 38, 18, 8},
                {0, 2, 0, 6, 3}
        };

        double[][] result = MatrixVectorOperations.transpose(matrix);
        double[][] expected = {
                {0, 3, 102, 0},
                {1, 234, 234, 2},
                {5, 12, 38, 0},
                {23, 5, 18, 6},
                {1, 3, 8, 3}
        };

        assertEquals("Unexpected result for transpose length", expected.length, result.length);
        for (int i = 0; i < expected.length; i++)
            assertArrayEquals("Unexpected result for matrix row " + (i + 1), expected[i], result[i], DELTA);
    }

    @Test
    public void testEuclideanDistance() {
        double[] vector0 = {82, 16, 2, 5, -2, 1};
        double[] vector1 = {5, 50, 4, 1, 8, -30};

        double result = MatrixVectorOperations.euclideanDistance(vector0, vector1);
        assertEquals("Unexpected result for euclidean vector distance", 90.36592278, result, 0.00000001);

        assertEquals(result, MatrixVectorOperations.euclideanDistance(vector1, vector0), DELTA);
    }

}