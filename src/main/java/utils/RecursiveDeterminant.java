package utils;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class RecursiveDeterminant {

    /**
     * Calculates the determinant of a 3x3 {@code matrix}
     *
     * @param matrix - the matrix to calculate the determinant for

     * @return - the determinant for the give {@code matrix}
     * @throws IllegalArgumentException - if the given {@code matrix} has a illegal size
     */
    public static int det2x2(int[][] matrix) {
        if (matrix.length != 2 || matrix[0].length != 2)
            throw new IllegalArgumentException("Illegal matrix size. Not 2x2");

        return matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];
    }

    /**
     * Calculates the determinant of a 3x3 {@code matrix}
     *
     * @param matrix - the matrix to calculate the determinant for

     * @return - the determinant for the give {@code matrix}
     * @throws IllegalArgumentException - if the given {@code matrix} has a illegal size
     */
    public static int det3x3(int[][] matrix) {
        if (matrix.length != 3 || matrix[0].length != 3)
            throw new IllegalArgumentException("Illegal matrix size. Not 3x3");

        int result = 0;

        int a = matrix[0][0];
        int b = matrix[0][1];
        int c = matrix[0][2];

        matrix = removeRow(matrix, 0);

        result += a * det2x2(removeColumn(matrix, 0));
        result -= b * det2x2(removeColumn(matrix, 1));
        result += c * det2x2(removeColumn(matrix, 2));

        return result;
    }

    /**
     * Calculates the determinant of a NxN {@code matrix}
     *
     * @param matrix - the matrix to calculate the determinant for

     * @return - the determinant for the given {@code matrix}
     * @throws IllegalArgumentException - if the given {@code matrix} has a illegal size
     */
    public static int detNxN(int[][] matrix) {
        if (matrix.length <= 1 || matrix.length != matrix[0].length)
            throw new IllegalArgumentException("Illegal matrix size");

        if (matrix.length == 2)
            return det2x2(matrix);

        int result = 0;

        int[][] matrixFirstRowRemoved = removeRow(matrix, 0);

        int mul = 1;
        for (int i = 0; i < matrix.length; i++) {
            int x = matrix[0][i];

            int[][] subMatrix = removeColumn(matrixFirstRowRemoved, i);

            result += mul * x * detNxN(subMatrix);

            mul *= -1;
        }

        return result;
    }

    /**
     * Removes the row with the index {@code rowIndex} from the given {@code matrix}
     *
     * @param matrix - the matrix from which a row should be removed
     * @param rowIndex - the index of the row which should be removed
     * @return - a matrix with row removed
     * @throws IllegalArgumentException - if the given {@code matrix} has a illegal size
     */
    @SuppressWarnings("Duplicates")
    public static int[][] removeRow(int[][] matrix, int rowIndex) {
        if (matrix.length == 0 || matrix[0].length == 0)
            throw new IllegalArgumentException("Illegal matrix size");

        int[][] newMatrix = new int[matrix.length - 1][matrix[0].length];

        for (int i = 0; i < matrix.length; i++) {
            if (i == rowIndex)
                continue;
            int insertIndex = i >= rowIndex? i - 1: i;

            for (int j = 0; j < matrix[i].length; j++)
                newMatrix[insertIndex][j] = matrix[i][j];
        }

        return newMatrix;
    }

    /**
     * Removes the column with the index {@code colIndex} from the given {@code matrix}
     *
     * @param matrix - the matrix from which a column should be removed
     * @param colIndex - the index of the column which should be removed
     * @return - a matrix with column removed
     * @throws IllegalArgumentException - if the given {@code matrix} has a illegal size
     */
    @SuppressWarnings("Duplicates")
    public static int[][] removeColumn(int[][] matrix, int colIndex) {
        if (matrix.length == 0 || matrix[0].length == 0)
            throw new IllegalArgumentException("Illegal matrix size");

        int[][] newMatrix = new int[matrix.length][matrix[0].length - 1];

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (j == colIndex)
                    continue;
                int insertIndex = j >= colIndex? j - 1: j;

                newMatrix[i][insertIndex] = matrix[i][j];
            }
        }

        return newMatrix;
    }

    public static void main(String[] args) {
        boolean unsuccessful = false;

        try {
            int determinant = det2x2(new int[][]{
                    {1, 2},
                    {3, 4}
            });

            assertEquals("Unexpected solution for 2x2 matrix", -2, determinant);
        } catch (Exception e) {
            e.printStackTrace();

            unsuccessful = true;
        }

        try {
            int[][] result = removeRow(new int[][]{
                    {1, 2, 3},
                    {4, 5, 6},
                    {7, 8, 9}
            }, 1);

            assertArrayEquals(new int[][] {
                    {1, 2, 3},
                    {7, 8, 9}
            }, result);
        } catch (Exception e) {
            e.printStackTrace();

            unsuccessful = true;
        }

        try {
            int[][] result = removeColumn(new int[][]{
                    {1, 2, 3},
                    {4, 5, 6},
                    {7, 8, 9}
            }, 1);

            assertArrayEquals(new int[][] {
                    {1, 3},
                    {4, 6},
                    {7, 9}
            }, result);
        } catch (Exception e) {
            e.printStackTrace();

            unsuccessful = true;
        }

        try {
            int determinant = det3x3(new int[][]{
                    {12, 5, 3},
                    {4, 52, 2},
                    {7, 8, 21}
            });

            assertEquals("Unexpected solution for 3x3 matrix", 11566, determinant);
        } catch (Exception e) {
            e.printStackTrace();

            unsuccessful = true;
        }

        try {
            int result = detNxN(new int[][]{
                    {1, 2, 3, 4},
                    {1, 3, 2, 4},
                    {4, 2, 1, 3},
                    {3, 4, 1, 2}
            });

            assertEquals(40, result);
        } catch (Exception e) {
            e.printStackTrace();

            unsuccessful = true;
        }

        try {
            int result = detNxN(new int[][]{
                    {1, 2, 8, 4, 6, 3, 5, 7},
                    {2, 6, 3, 4, 1, 8, 7, 5},
                    {3, 7, 1, 6, 4, 5, 8, 2},
                    {4, 8, 6, 5, 3, 2, 1, 7},
                    {5, 1, 4, 8, 2, 3, 6, 7},
                    {6, 4, 2, 5, 1, 3, 7, 8},
                    {7, 2, 6, 1, 5, 8, 3, 4},
                    {8, 6, 7, 4, 1, 2, 3, 5},
            });
            assertEquals(-4478112, result);
        } catch (Exception e) {
            e.printStackTrace();

            unsuccessful = true;
        }

        if (unsuccessful)
            System.err.println("Some/all tests returned with unexpected behaviour!");
    }

}