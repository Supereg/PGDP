public class ZAEQOp extends ZAExpression<Boolean> {

    private final ZAExpression<Integer> expression0;
    private final ZAExpression<Integer> expression1;

    public ZAEQOp(ZAExpression<Integer> expression0, ZAExpression<Integer> expression1) {
        this.expression0 = expression0;
        this.expression1 = expression1;
    }

    @Override
    public Boolean evaluate() {
        return expression0.evaluate().equals(expression1.evaluate());
    }

    @Override
    public String toString() {
        return "(" + expression0 + " == " + expression1 + ")";
    }

}