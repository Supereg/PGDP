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

    /*
    public static int[][] permutations(int n) {
        int length = 1;
        for (int n0 = n; n0 > 1; n0--) {
            length *= n0; // TODO
        }

        int[][] permutations = new int[length][n]; // TODO length
        // permutations[0] = 0, 1, ... n -1;
        for (int i = 0; i < n; i++) {
            permutations[0][i] = i;
        }


        return permutations;
    }

    private static void printMatrix(double[][] matrix) {
        for (double[] aMatrix : matrix) {
            for (double anAMatrix : aMatrix) {
                System.out.print(anAMatrix + " ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
       printMatrix(transpose(new double[][]{{2.0D, 3.0D, 4.0D},{3D, 4D, 2D}}));
    }*/

}