package utils;

import utils.RecursiveDeterminant;

import static org.junit.Assert.assertEquals;

public class RecursiveDeterminantTestSolution {
  
  @org.junit.Test
  public void testDetNxN() throws Exception {
    String msg = "Determinante falsch berechnet.";
    int[][] matrix;
    int expectedResult;
    int actualResult;

    matrix = new int[][] { { -1, -1 }, { -1, -1 } };
    expectedResult = 0;
    actualResult = RecursiveDeterminant.detNxN(matrix);
    assertEquals(msg, expectedResult, actualResult);

    matrix = new int[][] { { 1, 1 }, { 1, 1 } };
    expectedResult = 0;
    actualResult = RecursiveDeterminant.detNxN(matrix);
    assertEquals(msg, expectedResult, actualResult);

    matrix = new int[][] { { 3, 7, 9 }, { 1, -3, 5 }, { -8, 0, -2 } };
    expectedResult = -464;
    actualResult = RecursiveDeterminant.detNxN(matrix);
    assertEquals(msg, expectedResult, actualResult);

    matrix = new int[][] { { 0, 0, 0 }, { 0, 0, 0 }, { 0, 0, 0 } };
    expectedResult = 0;
    actualResult = RecursiveDeterminant.detNxN(matrix);
    assertEquals(msg, expectedResult, actualResult);

    matrix = new int[][] { { 0, 1, 2 }, { 3, 4, 5 }, { 6, 7, 8 } };
    expectedResult = 0;
    actualResult = RecursiveDeterminant.detNxN(matrix);
    assertEquals(msg, expectedResult, actualResult);

    matrix = new int[][] { { 0, 1, 2, 3 }, { 4, 5, 6, 7 }, { 8, 9, 10, 11 }, { 12, 13, 14, 15 } };
    expectedResult = 0;
    actualResult = RecursiveDeterminant.detNxN(matrix);
    assertEquals(msg, expectedResult, actualResult);

    matrix = new int[][] { { -1, 1, 2, 3 }, { 4, 5, 6, 7 }, { 8, 9, 10, 11 }, { 12, 13, 14, 15 } };
    expectedResult = 0;
    actualResult = RecursiveDeterminant.detNxN(matrix);
    assertEquals(msg, expectedResult, actualResult);

    matrix = new int[][] { { -1, 1, 2, 3 }, { 4, -5, 6, 7 }, { 8, 9, 10, 11 }, { 12, 13, 99, 15 } };
    expectedResult = -30470;
    actualResult = RecursiveDeterminant.detNxN(matrix);
    assertEquals(msg, expectedResult, actualResult);
  }

  public void assertMatrixEquals(String msg, int[][] expectedResult, int[][] actualResult) {
    StringBuilder matrixStr = new StringBuilder("\nExpected: ");
    matrixStr.append("\n[");
    for (int i = 0; i < expectedResult.length; i++) {
      matrixStr.append("\n[");
      for (int j = 0; j < expectedResult[i].length; j++)
        matrixStr.append(expectedResult[i][j] + ",");
      matrixStr.append("]");
    }
    matrixStr.append("\n]");

    matrixStr.append("\nActual:\n[");
    for (int i = 0; i < actualResult.length; i++) {
      matrixStr.append("\n[");
      for (int j = 0; j < actualResult[i].length; j++)
        matrixStr.append(actualResult[i][j] + ",");
      matrixStr.append("]");
    }
    matrixStr.append("\n]");

    for (int i = 0; i < expectedResult.length; i++)
      for (int j = 0; j < expectedResult[i].length; j++)
        if (expectedResult[i][j] != actualResult[i][j])
          throw new AssertionError(msg + matrixStr);
  }
  
  @org.junit.Test
  public void testDeleteColumn() throws Exception {
    String msg = "removeColumn() liefert falsches Ergebnis";
    int[][] matrix;
    int[][] expectedResult;
    int[][] actualResult;

    matrix = new int[][] { { 3, 7, 9 }, { 1, -3, 5 }, { -8, 0, -2 } };
    expectedResult = new int[][] { { 7, 9 }, { -3, 5 }, { 0, -2 } };
    actualResult = RecursiveDeterminant.removeColumn(matrix, 0);
    assertMatrixEquals(msg, expectedResult, actualResult);

    matrix = new int[][] { { 3, 7, 9 }, { 1, -3, 5 }, { -8, 0, -2 } };
    expectedResult = new int[][] { { 3, 9 }, { 1, 5 }, { -8, -2 } };
    actualResult = RecursiveDeterminant.removeColumn(matrix, 1);
    assertMatrixEquals(msg, expectedResult, actualResult);

    matrix = new int[][] { { 3, 7, 9 }, { 1, -3, 5 }, { -8, 0, -2 } };
    expectedResult = new int[][] { { 3, 7 }, { 1, -3 }, { -8, 0 } };
    actualResult = RecursiveDeterminant.removeColumn(matrix, 2);
    assertMatrixEquals(msg, expectedResult, actualResult);
  }
  
  @org.junit.Test
  public void testDeleteRow() throws Exception {
    String msg = "removeRow() liefert falsches Ergebnis";
    int[][] matrix;
    int[][] expectedResult;
    int[][] actualResult;

    matrix = new int[][] { { 3, 7, 9 }, { 1, -3, 5 }, { -8, 0, -2 } };
    expectedResult = new int[][] { { 1, -3, 5 }, { -8, 0, -2 } };
    actualResult = RecursiveDeterminant.removeRow(matrix, 0);
    assertMatrixEquals(msg, expectedResult, actualResult);

    matrix = new int[][] { { 3, 7, 9 }, { 1, -3, 5 }, { -8, 0, -2 } };
    expectedResult = new int[][] { { 3, 7, 9 }, { -8, 0, -2 } };
    actualResult = RecursiveDeterminant.removeRow(matrix, 1);
    assertMatrixEquals(msg, expectedResult, actualResult);

    matrix = new int[][] { { 3, 7, 9 }, { 1, -3, 5 }, { -8, 0, -2 } };
    expectedResult = new int[][] { { 3, 7, 9 }, { 1, -3, 5 } };
    actualResult = RecursiveDeterminant.removeRow(matrix, 2);
    assertMatrixEquals(msg, expectedResult, actualResult);
  }
}
