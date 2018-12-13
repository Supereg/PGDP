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
        ZAExpression<Boolean> exp = new ZAAndOp(
                new ZAGTOp(
                        new ZAMulOp(
                                new ZAConst<>(3),
                                new ZAConst<>(4)
                        ),
                        new ZAConst<>(2)
                ),
                new ZALTOp(
                        new ZAAddOp(
                                new ZAConst<>(22),
                                new ZAConst<>(88)
                        ),
                        new ZASubOp(
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
        ZAExpression<Boolean> exp = new ZAEQOp(
                new ZAAddOp(
                        new ZADivOp(
                                new ZAConst<>(12),
                                new ZAConst<>(3)
                        ),
                        new ZAConst<>(2)
                ),
                new ZASubOp(
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
        ZAExpression<Boolean> exp = new ZAOrOp(
                new ZALTOp(
                        new ZAConst<>(2),
                        new ZAConst<>(1)
                ),
                new ZAGTOp(
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
        ZAExpression<Integer> exp = new ZAMulOp(
                new ZAAddOp(
                        new ZASubOp(
                                new ZAConst<>(10),
                                new ZAConst<>(5)
                        ),
                        new ZAConst<>(1)
                ),
                new ZADivOp(
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
        ZAExpression<Boolean> exp = new ZANegOp<>(
                new ZAAndOp(
                        new ZAOrOp(
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
        ZAExpression<Integer> exp = new ZAAddOp(
                new ZAMulOp(
                        new ZAConst<>(15),
                        new ZAConst<>(30)
                ),
                new ZADivOp(
                        new ZAConst<>(38),
                        new ZAConst<>(2)
                )
        );

        int value = exp.evaluate();

        assertEquals(469, value);
        assertEquals("Unexpected #toString result for op2", "((15 * 30) + (38 / 2))", exp.toString());
    }

}