/**
 * Created by Andi on 18.11.18.
 */
public class MatrixVectorOperations {

    public static double[] multiply(double[][] matrix, double[] vector) {
        // Der erste Index adressiert die Zeile, der zweite Index die Spalte der Matrix.
        // Fehlerbehandlung ist nicht verlangt
        //if (matrix.length == 0 || matrix[0].length != vector.length)
        //    return null;

        double[] resultVector = new double[matrix.length];

        int columnLength = matrix[0].length;
        for (int i = 0; i < matrix.length; i++) {
            double iSum = 0;

            for (int j = 0; j < columnLength; j++) {
                iSum += matrix[i][j] * vector[j];
            }

            resultVector[i] = iSum;
        }

        return resultVector;
    }

    public static double cosineSimilarity(double[] v1, double[] v2) {
        if (v1.length != v2.length)
            return -1;
        if (v1.length == 0)
            return 0;

        double dotProduct = 0;
        for (int i = 0; i < v1.length; i++) {
            dotProduct += v1[i] * v2[i];
        }

        return dotProduct / Math.sqrt(dotProduct * dotProduct);
    }

    public static double[][] transpose(double[][] matrix) {
        int newRowLength = matrix.length == 0? 0: matrix[0].length;
        double[][] transposedMatrix = new double[newRowLength][matrix.length];

        for (int row = 0; row < matrix.length; row++) {
            for (int column = 0; column < matrix[0].length; column++) {
                transposedMatrix[column][row] = matrix[row][column];
            }
        }

        return transposedMatrix;
    }

    public static double euclideanDistance(double[] v1, double[] v2) {
        if (v1.length != v2.length)
            return -1;

        double length = 0;
        for (int i = 0; i < v1.length; i++) {
            length += Math.pow(v1[i] - v2[i], 2);
        }

        return Math.sqrt(length);
    }


    public static int[][] permutations(int n) {
        int[] possibilities = new int[n];

        int length = 1;
        for (int n0 = n; n0 >= 1; n0--) {
            length *= n0; // => n!
            possibilities[n0 - 1] = n0;
        }
        possibilities[n - 1] = n - 1; // decrement last one

        int[][] permutations = new int[length][n];

        int index = 0;

        int i;
        int value;
        boolean overflowed;
        while (true) {
            do {
                for (i = possibilities.length - 1; i >= 0; i--) {
                    value = possibilities[i] + 1;

                    overflowed = value > n;
                    possibilities[i] = overflowed? 1: value;

                    if (value <= n)
                        break;
                }
            } while (arrayIsMalformed(possibilities));

            if (index == length)
                break;

            permutations[index++] = arrayCopy(possibilities);
        }

        return permutations;
    }

    private static boolean arrayIsMalformed(int[] array) {
        int iValue;
        int jValue;
        for (int i = 0; i < array.length; i++) {
            iValue = array[i];

            if (iValue == 0) // musn't contain zeros
                return true;

            for (int j = 0; j < array.length; j++) {
                if (j == i)
                    continue;

                jValue = array[j];

                if (jValue == iValue) // musn't contain a digit more than one time
                    return true;
            }
        }

        return false;
    }

    public static int sgn(int[] permutation) {
        int n = permutation.length;

        long numerator = 1;
        // long cause this can get pretty big very fast and thus overflow or even get 0, see last test example
        long denominator = 1;

        for (int i = 1; i <= n; i++) {
            for (int j = i + 1; j <= n; j++) {
                numerator *= permutation[j - 1] - permutation[i - 1];
                denominator *= j - i;
            }
        }

        return (int) (numerator / denominator);
    }

    public static int determinant(int[][] A) {
        int[][] permutations = permutations(A.length);

        int sum = 0;
        for (int[] p: permutations) {
            int sgn = sgn(p);

            int mul = 1;
            for (int i = 0; i < A.length; i++) {
                mul *= A[i][p[i] - 1];
            }

            sum += sgn * mul;
        }

        return sum;
    }

    private static int[] arrayCopy(int[] array) {
        int[] newArray = new int[array.length];
        System.arraycopy(array, 0, newArray, 0, array.length);
        return newArray;
    }

    /* Debug helper method
    private static void printMatrix(int[][] matrix) {
        for (int[] aMatrix : matrix) {
            for (int anAMatrix : aMatrix) {
                System.out.print(anAMatrix + " ");
            }
            System.out.println();
        }
    }
    */

}