import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ZATest {

    public static void main(String[] args) {
        ZATest test = new ZATest();

        try {
            test.testOp0();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        try {
            test.testOp1();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        try {
            test.testOp2();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        try {
            test.testZAOp0();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        try {
            test.testZAOp1();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        try {
            test.testZAOp2();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testZAOp0() {
        ZAExpression<Boolean> exp = new ZAAndOp<Boolean, Boolean>(
                new ZAGTOp<Integer, Boolean>(
                        new ZAMulOp<Integer, Integer>(
                                new ZAConst<>(3),
                                new ZAConst<>(4)
                        ),
                        new ZAConst<>(2)
                ),
                new ZALTOp<Integer, Boolean>(
                        new ZAAddOp<Integer, Integer>(
                                new ZAConst<>(22),
                                new ZAConst<>(88)
                        ),
                        new ZASubOp<Integer, Integer>(
                                new ZAConst<>(3000),
                                new ZAConst<>(100)
                        )
                )
        );

        boolean result = exp.evaluate();

        assertTrue("Unexpected evaluation of zaop0", result);
        assertEquals("Unexpected #toString result for zaop0", "(((3 * 4) > 2) & ((22 + 88) < (3000 - 100)))", exp.toString());
    }

    @Test
    public void testZAOp1() {
        ZAExpression<Boolean> exp = new ZAEQOp<Integer, Boolean>(
                new ZAAddOp<Integer, Integer>(
                        new ZADivOp<Integer, Integer>(
                                new ZAConst<>(12),
                                new ZAConst<>(3)
                        ),
                        new ZAConst<>(2)
                ),
                new ZASubOp<Integer, Integer>(
                        new ZAConst<>(12),
                        new ZAConst<>(6)
                )
        );

        boolean result = exp.evaluate();

        assertTrue("Unexpected evaluation of zaop1", result);
        assertEquals("Unexpected #toString result for zaop1", "(((12 / 3) + 2) == (12 - 6))", exp.toString());
    }

    @Test
    public void testZAOp2() {
        ZAExpression<Boolean> exp = new ZAOrOp<Boolean, Boolean>(
                new ZALTOp<Integer, Boolean>(
                        new ZAConst<>(2),
                        new ZAConst<>(1)
                ),
                new ZAGTOp<Integer, Boolean>(
                        new ZAConst<>(12),
                        new ZAConst<>(2)
                )
        );

        boolean result = exp.evaluate();

        assertTrue("Unexpected evaluation of zaop2", result);
        assertEquals("Unexpected #toString result for zaop2", "((2 < 1) | (12 > 2))", exp.toString());
    }

    @Test
    public void testOp0() {
        ZAExpression<Integer> exp = new ZAMulOp<Integer, Integer>(
                new ZAAddOp<Integer, Integer>(
                        new ZASubOp<Integer, Integer>(
                                new ZAConst<>(10),
                                new ZAConst<>(5)
                        ),
                        new ZAConst<>(1)
                ),
                new ZADivOp<Integer, Integer>(
                        new ZAConst<>(8),
                        new ZAConst<>(2)
                )
        );

        int value = exp.evaluate();

        assertEquals("Unexpected evaluation of op0", 24, value);
        assertEquals("Unexpected #toString result for op0", "(((10 - 5) + 1) * (8 / 2))", exp.toString());
    }

    @Test
    public void testOp1() {
        ZAExpression<Boolean> exp = new ZANegOp<Boolean>(
                new ZAAndOp<Boolean, Boolean>(
                        new ZAOrOp<Boolean, Boolean>(
                                new ZAConst<>(false),
                                new ZAConst<>(true)
                        ),
                        new ZAConst<>(false)
                )
        );

        boolean value = exp.evaluate();

        assertTrue("Unexpected evaluation of op1", value);
        assertEquals("Unexpected #toString result for op1", "(!((false | true) & false))", exp.toString());
    }

    @Test
    public void testOp2() {
        ZAExpression<Integer> exp = new ZAAddOp<Integer, Integer>(
                new ZAMulOp<Integer, Integer>(
                        new ZAConst<>(15),
                        new ZAConst<>(30)
                ),
                new ZADivOp<Integer, Integer>(
                        new ZAConst<>(38),
                        new ZAConst<>(2)
                )
        );

        int value = exp.evaluate();

        assertEquals(469, value);
        assertEquals("Unexpected #toString result for op2", "((15 * 30) + (38 / 2))", exp.toString());
    }

}