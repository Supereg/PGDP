import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ATest {

    public static void main(String[] args) {
        ATest test = new ATest();

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
    }

    @Test
    public void testOp0() {
        Expression<Integer> exp = new MulOp(
                new AddOp(
                        new SubOp(
                                new Const<>(10),
                                new Const<>(5)
                        ),
                        new Const<>(1)
                ),
                new DivOp(
                        new Const<>(8),
                        new Const<>(2)
                )
        );

        int value = exp.evaluate();

        assertEquals("Unexpected evaluation of op0", 24, value);
        assertEquals("Unexpected #toString result for op0", "(((10 - 5) + 1) * (8 / 2))", exp.toString());
    }

    @Test
    public void testOp1() {
        Expression<Boolean> exp = new NotOp(
                new AndOp(
                        new OrOp(
                                new Const<>(false),
                                new Const<>(true)
                        ),
                        new Const<>(false)
                )
        );

        boolean value = exp.evaluate();

        assertTrue("Unexpected evaluation of op1", value);
        assertEquals("Unexpected #toString result for op1", "(!((false | true) & false))", exp.toString());
    }

    @Test
    public void testOp2() {
        Expression<Integer> exp = new AddOp(
                new MulOp(
                        new Const<>(15),
                        new Const<>(30)
                ),
                new DivOp(
                        new Const<>(38),
                        new Const<>(2)
                )
        );

        int value = exp.evaluate();

        assertEquals(469, value);
        assertEquals("Unexpected #toString result for op2", "((15 * 30) + (38 / 2))", exp.toString());
    }

}