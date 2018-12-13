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
        return dotProduct(v1, v2) / Math.sqrt(dotProduct(v1, v1) * dotProduct(v2, v2));
    }

    private static double dotProduct(double[] v1, double[] v2) {
        if (v1.length != v2.length)
            return -1;
        if (v1.length == 0)
            return 0;

        double dotProduct = 0;
        for (int i = 0; i < v1.length; i++) {
            dotProduct += v1[i] * v2[i];
        }

        return dotProduct;
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

        int permutationCount = 1;
        for (int i = n; i >= 1; i--) {
            permutationCount *= i;
            possibilities[i - 1] = i - 1;
        }

        int[][] permutations = new int[permutationCount][n];

        int index = 0;
        do {
            int[] permutation = new int[possibilities.length];
            System.arraycopy(possibilities, 0, permutation, 0, possibilities.length);
            permutations[index++] = permutation;
        } while (nextPossibilityExists(possibilities));

        return permutations;
    }

    private static boolean nextPossibilityExists(int[] countVariables) {
        for (int i = countVariables.length - 1; i >= 0; i--) {
            int variable = countVariables[i];

            while (++variable < countVariables.length) {
                if (!existsInArrayBeforeIndex(variable, countVariables, i)) {
                    countVariables[i] = variable;

                    for (int j = i + 1; j < countVariables.length; j++)
                        insertElementAtIndexNotOccurringBefore(j, countVariables);
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean existsInArrayBeforeIndex(int element, int[] array, int index) {
        for (int i = 0; i < index; i++)
            if (array[i] == element)
                return true;

        return false;
    }

    private static void insertElementAtIndexNotOccurringBefore(int index, int[] array) {
        int n;
        for (n = 0; n < array.length; n++) {
            boolean passed = true;

            for (int i = 0; i < index; i++) {
                int arrayElement = array[i];
                if (n == arrayElement)
                    passed = false;
            }

            if (passed)
                break;
        }

        if (n <= array.length)
            array[index] = n;
        else
            System.err.println("Illegal setup detected for array. No possible element found to insert!");
    }

    public static int sgn(int[] permutation) {
        int n = permutation.length;

        long numerator = 1;
        // long, cause this can get pretty damn big very fast and thus overflow or even get 0, see last test example
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
                mul *= A[i][p[i]];
            }

            sum += sgn * mul;
        }

        return sum;
    }

    // Debug helper method
    public static void printMatrix(int[][] matrix) {
        for (int[] aMatrix : matrix) {
            for (int anAMatrix : aMatrix) {
                System.out.print(anAMatrix + " ");
            }
            System.out.println();
        }
    }

}