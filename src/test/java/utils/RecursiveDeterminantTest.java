package utils;

import org.junit.Test;
import utils.RecursiveDeterminant;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class RecursiveDeterminantTest {

    @Test
    public void test2x2() {
        int determinant = RecursiveDeterminant.det2x2(new int[][]{
                {1, 2},
                {3, 4}
        });

        assertEquals("Unexpected solution for 2x2 matrix", -2, determinant);
    }

    @Test
    public void testRemoveRow() {
        int[][] result = RecursiveDeterminant.removeRow(new int[][]{
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 9}
        }, 1);

        assertArrayEquals(new int[][] {
                {1, 2, 3},
                {7, 8, 9}
        }, result);
    }

    @Test
    public void testRemoveColumn() {
        int[][] result = RecursiveDeterminant.removeColumn(new int[][]{
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 9}
        }, 1);

        assertArrayEquals(new int[][] {
                {1, 3},
                {4, 6},
                {7, 9}
        }, result);
    }

    @Test
    public void test3x3() {
        int determinant = RecursiveDeterminant.det3x3(new int[][]{
                {12, 5, 3},
                {4, 52, 2},
                {7, 8, 21}
        });

        assertEquals("Unexpected solution for 3x3 matrix", 11566, determinant);
    }

    @Test
    public void testNxN0() {
        int result = RecursiveDeterminant.detNxN(new int[][]{
                {1, 2, 3, 4},
                {1, 3, 2, 4},
                {4, 2, 1, 3},
                {3, 4, 1, 2}
        });

        assertEquals(40, result);
    }

    @Test
    public void testNxN1() {
        //noinspection Duplicates
        int result = RecursiveDeterminant.detNxN(new int[][]{
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
    }

}